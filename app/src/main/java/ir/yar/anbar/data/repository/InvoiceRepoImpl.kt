package ir.yar.anbar.data.repository

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.local.dao.InvoiceDao
import ir.yar.anbar.data.local.dao.InvoiceProductDao
import ir.yar.anbar.data.local.datastore.SyncCursorStore
import ir.yar.anbar.data.local.datasource.UserProductLocalDataSource
import ir.yar.anbar.data.local.entity.InvoiceEntity
import ir.yar.anbar.data.local.relation.InvoiceWithProductsRelation
import ir.yar.anbar.data.mapper.toCrossRef
import ir.yar.anbar.data.mapper.toDomain
import ir.yar.anbar.data.mapper.toEntity
import ir.yar.anbar.data.mapper.toSyncRequest
import ir.yar.anbar.data.mapper.toTombstoneRequest
import ir.yar.anbar.data.remote.datasource.InvoiceRemoteDataSource
import ir.yar.anbar.data.remote.dto.request.InvoiceSyncRequestDto
import ir.yar.anbar.data.remote.dto.response.InvoiceResponseDto
import ir.yar.anbar.domain.model.Invoice
import ir.yar.anbar.domain.model.InvoiceSyncResult
import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.model.TopSellingProductInfo
import ir.yar.anbar.domain.model.toDomain
import ir.yar.anbar.domain.model.toEntity
import ir.yar.anbar.domain.repository.InvoiceRepository
import ir.yar.anbar.di.ApplicationScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

class InvoiceRepoImpl @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val invoiceProductDao: InvoiceProductDao,
    private val userProductLocalDataSource: UserProductLocalDataSource,
    private val remoteDataSource: InvoiceRemoteDataSource,
    private val syncCursorStore: SyncCursorStore,
    @ApplicationScope private val applicationScope: CoroutineScope
) : InvoiceRepository {

    // Server caps a push batch at 1000 invoices per request
    private val maxPushBatch = 1000
    private val pullPageSize = 50

    // One sync pass at a time; getAllInvoices() also triggers background pulls
    private val syncMutex = Mutex()

    override suspend fun createInvoice(invoice: Invoice): Long {
        return invoiceDao.insertInvoice(invoice.toEntity())
    }

    /**
     * Deletion is offline-first like everything else:
     * - never pushed (serverId == null) → hard delete, nothing to sync
     * - already on the server → tombstone (isDeleted = 1, hidden from UI and
     *   analytics) that the next sync pushes before the row is removed
     */
    override suspend fun deleteInvoice(invoiceId: Long) {
        val invoice = invoiceDao.getInvoiceById(invoiceId) ?: return

        if (invoice.serverId == null) {
            invoiceDao.deleteInvoice(invoiceId)
            return
        }

        // Items go immediately: the server clears them when it applies the
        // deletion, so pushing them again would be wasted payload
        invoiceProductDao.deleteCrossRefsForInvoice(invoiceId)
        invoiceDao.markInvoiceDeletedForSync(invoiceId, System.currentTimeMillis())
    }

    override suspend fun syncInvoices(): InvoiceSyncResult {
        syncMutex.lock()
        try {
            val push = pushPendingInvoices()
            val pull = pullInvoicesFromServer()
            return InvoiceSyncResult(
                pushed = push.pushed,
                deleted = push.deleted,
                pulled = pull.pulled,
                skipped = push.skipped + pull.skipped,
                failed = push.failed + pull.failed
            )
        } finally {
            syncMutex.unlock()
        }
    }

    //region Push

    private data class PushStats(
        val pushed: Int = 0,
        val deleted: Int = 0,
        val skipped: Int = 0,
        val failed: Int = 0
    )

    private suspend fun pushPendingInvoices(): PushStats {
        val pending = invoiceDao.getUnsyncedInvoices()
        if (pending.isEmpty()) return PushStats()

        // Resolve every pending invoice's items once; invoices whose products
        // haven't been pushed yet wait for the product sync to catch up
        val crossRefsByInvoiceId = pending.associate { invoice ->
            invoice.id to invoiceProductDao.getCrossRefsForInvoice(invoice.id)
        }
        val localProductIds = crossRefsByInvoiceId.values
            .asSequence()
            .flatMap { refs -> refs.asSequence().map { it.productId } }
            .distinct()
            .toList()
        val serverIdByLocalProductId = if (localProductIds.isEmpty()) emptyMap()
        else userProductLocalDataSource.getProductsByIds(localProductIds)
            .mapNotNull { product ->
                product.serverId?.let { product.id to it }
            }
            .toMap()

        var pushed = 0
        var deleted = 0
        var skipped = 0
        var failed = 0

        pending.chunked(maxPushBatch).forEach { chunk ->
            val batch = mutableListOf<InvoiceSyncRequestDto>()
            val batchedInvoices = mutableListOf<InvoiceEntity>()

            for (invoice in chunk) {
                if (invoice.isDeleted) {
                    batch += invoice.toTombstoneRequest()
                    batchedInvoices += invoice
                    continue
                }

                val refs = crossRefsByInvoiceId[invoice.id].orEmpty()
                if (refs.any { serverIdByLocalProductId[it.productId] == null }) {
                    skipped++
                    continue
                }
                batch += invoice.toSyncRequest(refs) { localProductId ->
                    // Total by construction — every item resolved above
                    serverIdByLocalProductId.getValue(localProductId)
                }
                batchedInvoices += invoice
            }

            if (batch.isEmpty()) return@forEach

            try {
                val response = remoteDataSource.pushInvoices(batch)
                val mappingsByLocalId = (response as? ApiResponse.Success)
                    ?.data?.data?.associateBy { it.localId }

                if (mappingsByLocalId == null) {
                    failed += batch.size
                    return@forEach
                }

                for (invoice in batchedInvoices) {
                    val serverId = mappingsByLocalId[invoice.id]?.serverId ?: continue
                    if (invoice.isDeleted) {
                        // Deletion confirmed by the server → drop the tombstone
                        invoiceDao.deleteInvoice(invoice.id)
                        deleted++
                    } else {
                        invoiceDao.markInvoiceSynced(invoice.id, serverId, System.currentTimeMillis())
                        pushed++
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failed += batch.size
            }
        }

        return PushStats(pushed, deleted, skipped, failed)
    }

    //endregion

    //region Pull

    private data class PullStats(
        val pulled: Int = 0,
        val skipped: Int = 0,
        val failed: Int = 0
    )

    private suspend fun pullInvoicesFromServer(): PullStats {
        var pulled = 0
        var skipped = 0
        var failed = 0

        try {
            val since = syncCursorStore.invoiceSyncCursor()
            var page = 0
            var latestServerTime = -1L

            while (true) {
                val response = remoteDataSource.pullInvoices(since, page, pullPageSize)
                val pullData = (response as? ApiResponse.Success)?.data?.data
                if (pullData == null) {
                    failed++
                    break
                }
                latestServerTime = pullData.serverTime

                val merge = mergeServerInvoices(pullData.content)
                pulled += merge.merged
                skipped += merge.skipped

                if (pullData.last || pullData.content.isEmpty()) break
                page++
            }

            // The cursor only moves when everything merged: a skipped invoice
            // (its product isn't synced locally yet) is retried on the next
            // sync, after the product sync has caught up. Already-merged rows
            // being re-sent is harmless — merges are upserts by serverId.
            if (skipped == 0 && latestServerTime > 0) {
                syncCursorStore.saveInvoiceSyncCursor(latestServerTime)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            failed++
        }

        return PullStats(pulled, skipped, failed)
    }

    private data class MergeStats(val merged: Int = 0, val skipped: Int = 0)

    /**
     * Merge policy per server row, matched by serverId:
     * - deleted on the server → remove the local row (mirrors the deletion)
     * - unknown locally → insert as a synced row
     * - local row already synced → take server values (server wins)
     * - local row pending a push → keep local, it wins until pushed
     * Invoices whose items reference products unknown locally are skipped so
     * they aren't stored with missing lines.
     */
    private suspend fun mergeServerInvoices(dtos: List<InvoiceResponseDto>): MergeStats {
        if (dtos.isEmpty()) return MergeStats()

        val localByServerId = invoiceDao.getInvoicesByServerIds(dtos.map { it.id })
            .associateBy { it.serverId }

        val serverProductIds = dtos
            .filter { !(it.isDeleted ?: false) }
            .flatMap { dto -> dto.items.orEmpty().map { it.productId } }
            .distinct()
        val localIdByServerProductId = if (serverProductIds.isEmpty()) emptyMap()
        else userProductLocalDataSource.getProductsByServerIds(serverProductIds)
            .mapNotNull { product ->
                product.serverId?.let { it to product.id }
            }
            .toMap()

        var merged = 0
        var skipped = 0

        for (dto in dtos) {
            val local = localByServerId[dto.id]

            if (dto.isDeleted == true) {
                if (local != null && (local.synced || local.isDeleted)) {
                    invoiceProductDao.deleteCrossRefsForInvoice(local.id)
                    invoiceDao.deleteInvoice(local.id)
                }
                // A pending local row is left alone: its push re-creates the
                // server copy with a newer updatedAt
                merged++
                continue
            }

            val items = dto.items.orEmpty()
            val allItemsResolvable = items.all { localIdByServerProductId.containsKey(it.productId) }
            if (!allItemsResolvable) {
                // Stored with missing lines or dropped from an existing
                // invoice — either way corrupt, so retry after product sync
                skipped++
                continue
            }

            if (local != null && !local.synced && !local.isDeleted) {
                // Pending local change wins until it's pushed
                merged++
                continue
            }

            // Reuse the existing row's id so the REPLACE insert updates it
            val invoiceId = invoiceDao.insertInvoice(dto.toEntity(localId = local?.id ?: 0L))
            invoiceProductDao.deleteCrossRefsForInvoice(invoiceId)
            invoiceProductDao.insertCrossRefs(items.map { item ->
                item.toCrossRef(
                    invoiceId = invoiceId,
                    localProductId = localIdByServerProductId.getValue(item.productId)
                )
            })
            merged++
        }

        return MergeStats(merged, skipped)
    }

    //endregion

    override fun getInvoiceWithProducts(invoiceId: Long): Flow<InvoiceWithProducts> {
        return invoiceDao.getInvoiceWithProducts(invoiceId).map { relation ->
            mapToInvoiceWithProducts(relation)
        }
    }

    override fun getAllInvoices(): Flow<List<InvoiceWithProducts>> {
        // Local-first: DB rows emit immediately. In parallel, pull the server's
        // copy so invoices created on other devices appear here through
        // Room's reactive flow. Same pattern as ProductRepoImpl.
        applicationScope.launch { refreshInvoicesFromServer() }
        return invoiceDao.getAllInvoiceWithProducts().map { list ->
            list.map { mapToInvoiceWithProducts(it) }
        }
    }

    override fun getAllInvoicesOldestFirst(): Flow<List<InvoiceWithProducts>> {
        return invoiceDao.getAllInvoiceWithProductsOldestFirst().map { list ->
            list.map { mapToInvoiceWithProducts(it) }
        }
    }

    /**
     * Pull-only pass for background refreshes. Never blocks callers on
     * failures — the local table stays as-is.
     */
    private suspend fun refreshInvoicesFromServer() {
        if (!syncMutex.tryLock()) return
        try {
            pullInvoicesFromServer()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Offline / server error — keep serving local data
        } finally {
            syncMutex.unlock()
        }
    }

    override suspend fun getNextInvoiceNumberId(): Long {
        val lastInvoice = invoiceDao.getLastInvoice()
        return if (lastInvoice != null) {
            lastInvoice.invoiceNumber + 1
        } else {
            1000 // Start from 1000 if no invoices exist
        }
    }

    // Analytics methods
    override suspend fun getTotalSalesForMonth(yearMonth: String): Long {
        return invoiceDao.getTotalSalesForMonth(yearMonth)
    }

    override suspend fun getTotalInvoicesForMonth(yearMonth: String): Int {
        return invoiceDao.getTotalInvoicesForMonth(yearMonth)
    }

    override suspend fun getTotalQuantityForMonth(yearMonth: String): Int {
        return invoiceDao.getTotalQuantityForMonth(yearMonth)
    }

    override suspend fun getTopSellingProductsForMonth(yearMonth: String): List<TopSellingProductInfo> {
        return invoiceDao.getTopSellingProductsForMonth(yearMonth).map {
            TopSellingProductInfo(
                name = it.name,
                totalQuantity = it.totalQuantity,
                totalSales = it.totalSales
            )
        }
    }

    override fun getTotalProfitBetweenDates(start: Long, end: Long): Flow<Long> {
        return invoiceDao.getTotalProfitBetweenDates(start, end)
    }

    override fun getTotalSalesBetweenDates(start: Long, end: Long): Flow<Long> {
        return invoiceDao.getTotalSalesBetweenDates(start, end)
    }

    override fun getTotalInvoicesBetweenDates(start: Long, end: Long): Flow<Int> {
        return invoiceDao.getTotalInvoicesBetweenDates(start, end)
    }

    private fun mapToInvoiceWithProducts(
        invoiceWithProductsRelation: InvoiceWithProductsRelation
    ): InvoiceWithProducts {
        val invoice = invoiceWithProductsRelation.invoice.toDomain()
        val products = invoiceWithProductsRelation.invoiceProducts.map { it.product.toDomain() }
        val invoiceProducts = invoiceWithProductsRelation.invoiceProducts.map {
            it.invoiceProductsCrossRef.toDomain()
        }

        return InvoiceWithProducts(
            invoice = invoice,
            invoiceProducts = invoiceProducts,
            products = products
        )
    }
}