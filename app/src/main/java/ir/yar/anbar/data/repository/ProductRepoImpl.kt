package ir.yar.anbar.data.repository

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.local.datasource.UserProductLocalDataSource
import ir.yar.anbar.data.local.entity.UserProductEntity
import ir.yar.anbar.data.mapper.mergeInto
import ir.yar.anbar.data.mapper.toDomain
import ir.yar.anbar.data.mapper.toEntity
import ir.yar.anbar.data.mapper.toNewEntity
import ir.yar.anbar.data.mapper.toRequestDto
import ir.yar.anbar.data.remote.datasource.UserProductRemoteDataSource
import ir.yar.anbar.data.remote.dto.response.UserProductResponseDto
import ir.yar.anbar.data.util.ProductImageFileManager
import ir.yar.anbar.di.ApplicationScope
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSyncResult
import ir.yar.anbar.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val localDataSource: UserProductLocalDataSource,
    private val remoteDataSource: UserProductRemoteDataSource,
    private val imageFileManager: ProductImageFileManager,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ProductRepository {

    private val refreshMutex = Mutex()

    override suspend fun addProduct(product: Product, imageSource: String?) {
        // 1. Save locally first (offline-first). The row starts as PENDING_CREATE
        //    and is only marked SYNCED after the server confirms it.
        val localId = localDataSource.insertProduct(
            product.toEntity().copy(
                serverId = null,
                syncStatus = UserProductEntity.SYNC_STATUS_PENDING_CREATE,
                synced = false
            )
        )

        // 2. Push to the server. Any failure keeps the local row unsynced
        //    so a future sync pass can retry it.
        try {
            val response = remoteDataSource.createCustomProduct(
                product = product.toRequestDto(),
                imageSource = imageSource ?: product.image?.localUri
            )
            val serverId = (response as? ApiResponse.Success)?.data?.data ?: return
            localDataSource.markProductSynced(
                localId = localId,
                serverId = serverId
            )
        } catch (e: Exception) {
            // Network call threw — local row remains PENDING_CREATE
        }
    }

    override fun getAllProducts(): Flow<List<Product>> {
        // Local-first: DB rows emit immediately. In parallel, pull the server's copy
        // so products created on other devices get inserted/updated locally and
        // appear here through Room's reactive flow.
        applicationScope.launch { refreshProductsFromServer() }
        return localDataSource.observeAllProducts()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        // Local-first read; same reasoning as getAllProducts().
        return localDataSource.searchProducts(query)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun deleteProduct(product: Product) {
        val existing = localDataSource.getProductById(product.id.value) ?: return

        // Never pushed to the server — safe to hard-delete locally
        val serverId = existing.serverId
        if (serverId == null) {
            localDataSource.deleteProduct(existing)
            return
        }

        // Delete on the server first; on any failure soft-delete locally as
        // PENDING_DELETE so a future sync pass can retry the server delete.
        try {
            val response = remoteDataSource.deleteProduct(serverId)
            if ((response as? ApiResponse.Success)?.data?.success == true) {
                localDataSource.deleteProduct(existing)
            } else {
                localDataSource.markProductPendingDelete(existing.id)
            }
        } catch (e: Exception) {
            localDataSource.markProductPendingDelete(existing.id)
        }
    }

    override suspend fun editProduct(product: Product) {
        updateProductAndSync(product)
    }

    override suspend fun updateProduct(product: Product): Int {
        return updateProductAndSync(product)
    }

    /**
     * Updates the row locally as PENDING_UPDATE (preserving the server link),
     * then pushes the change to the server. Any failure keeps the row pending.
     */
    private suspend fun updateProductAndSync(product: Product): Int {
        val existing = localDataSource.getProductById(product.id.value)
        val serverId = existing?.serverId

        val rowsUpdated = localDataSource.updateProduct(
            product.toEntity().copy(
                serverId = serverId,
                syncStatus = UserProductEntity.SYNC_STATUS_PENDING_UPDATE,
                synced = false
            )
        )

        if (serverId == null) return rowsUpdated // local-only product, nothing to push

        try {
            val response = remoteDataSource.updateProduct(
                id = serverId,
                product = product.toRequestDto(),
                imageSource = product.image?.localUri
            )
            if ((response as? ApiResponse.Success)?.data?.success == true) {
                localDataSource.markProductSynced(
                    localId = product.id.value,
                    serverId = serverId
                )
            }
        } catch (e: Exception) {
            // Network call threw — local row remains PENDING_UPDATE
        }
        return rowsUpdated
    }

    override suspend fun getProductById(id: Long): Product? {
        val entity = localDataSource.getProductById(id) ?: return null

        // Local-first; if the row is linked to a server copy, best-effort refresh
        // with the server version. Offline failures fall back to the local row.
        val serverId = entity.serverId
        if (serverId != null) {
            try {
                val response = remoteDataSource.getProductById(serverId)
                val serverProduct = (response as? ApiResponse.Success)?.data
                if (serverProduct != null) {
                    val merged = serverProduct.mergeInto(entity, serverProduct.persistImage())
                    if (merged != entity) {
                        localDataSource.updateProduct(merged)
                    }
                }
            } catch (e: Exception) {
                // Offline — serve the local row as-is
            }
        }
        return localDataSource.getProductById(id)?.toDomain()
    }

    override suspend fun getProductsByIds(ids: List<Long>): List<Product> {
        return localDataSource.getProductsByIds(ids).map { it.toDomain() }
    }

    override fun getProductsLowStock(stockLimit: Int): Flow<List<Product>> {
        return localDataSource.observeLowStockProducts(stockLimit)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun syncAllProducts(): ProductSyncResult {
        var created = 0
        var updated = 0
        var deleted = 0
        var failed = 0

        // 1. Push products that were never confirmed by the server
        for (entity in localDataSource.getPendingCreateProducts()) {
            try {
                val response = remoteDataSource.createCustomProduct(
                    product = entity.toDomain().toRequestDto(),
                    imageSource = entity.imageLocalPath
                )
                val serverId = (response as? ApiResponse.Success)?.data?.data
                if (serverId != null) {
                    localDataSource.markProductSynced(entity.id, serverId)
                    created++
                } else {
                    failed++
                }
            } catch (e: Exception) {
                failed++
            }
        }

        // 2. Push local edits of already-synced products
        for (entity in localDataSource.getPendingUpdateProducts()) {
            val serverId = entity.serverId
            if (serverId == null) {
                // Row lost its server link — leave it pending for manual inspection
                failed++
                continue
            }
            try {
                val response = remoteDataSource.updateProduct(
                    id = serverId,
                    product = entity.toDomain().toRequestDto(),
                    imageSource = entity.imageLocalPath
                )
                if ((response as? ApiResponse.Success)?.data?.success == true) {
                    localDataSource.markProductSynced(entity.id, serverId)
                    updated++
                } else {
                    failed++
                }
            } catch (e: Exception) {
                failed++
            }
        }

        // 3. Push deletions that previously failed on the server
        for (entity in localDataSource.getPendingDeleteProducts()) {
            val serverId = entity.serverId
            if (serverId == null) {
                // Was never on the server — just drop the soft-deleted row
                localDataSource.deleteProduct(entity)
                deleted++
                continue
            }
            try {
                val response = remoteDataSource.deleteProduct(serverId)
                if ((response as? ApiResponse.Success)?.data?.success == true) {
                    localDataSource.deleteProduct(entity)
                    deleted++
                } else {
                    failed++
                }
            } catch (e: Exception) {
                failed++
            }
        }

        return ProductSyncResult(
            created = created,
            updated = updated,
            deleted = deleted,
            failed = failed
        )
    }

    /**
     * Pulls every page of the user's products from the server and merges them into
     * the local DB. Never blocks callers on failures — the local table stays as-is.
     */
    private suspend fun refreshProductsFromServer() {
        // Only one pull at a time; getAllProducts() is called on every list load
        if (!refreshMutex.tryLock()) return
        try {
            val pageSize = 50
            var page = 0
            while (true) {
                val response = remoteDataSource.getUserProducts(page, pageSize)
                val pageData = (response as? ApiResponse.Success)?.data ?: break
                mergeServerProducts(pageData.content)
                if (pageData.last || pageData.content.isEmpty()) break
                page++
            }
        } catch (e: Exception) {
            // Offline / server error — keep serving local data
        } finally {
            refreshMutex.unlock()
        }
    }

    /**
     * Merge policy per server row, matched by serverId:
     * - unknown locally → insert as a synced row
     * - local row already synced → take server values (server wins)
     * - local row pending (create/update/delete) → keep local, it wins until pushed
     */
    private suspend fun mergeServerProducts(serverProducts: List<UserProductResponseDto>) {
        if (serverProducts.isEmpty()) return

        val localByServerId = localDataSource
            .getProductsByServerIds(serverProducts.map { it.id })
            .associateBy { it.serverId }

        for (dto in serverProducts) {
            val local = localByServerId[dto.id]
            when {
                local == null -> localDataSource.insertProduct(
                    dto.toNewEntity(dto.persistImage())
                )
                local.syncStatus == UserProductEntity.SYNC_STATUS_SYNCED -> {
                    val merged = dto.mergeInto(local, dto.persistImage())
                    if (merged != local) {
                        localDataSource.updateProduct(merged)
                    }
                }
                // else: local pending change — leave untouched until it's pushed
            }
        }
    }

    /**
     * Decodes the Base64 image the server sent with a product into a local file
     * so it can be displayed. Never throws — a broken image must not break the
     * product sync itself.
     */
    private suspend fun UserProductResponseDto.persistImage(): String? = runCatching {
        imageFileManager.saveServerImage(id, image, imageType)
    }.getOrNull()

}
