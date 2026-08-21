package ir.yar.anbar.data.repository

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.local.datasource.UserProductLocalDataSource
import ir.yar.anbar.data.local.entity.UserProductEntity
import ir.yar.anbar.data.mapper.mergeInto
import ir.yar.anbar.data.mapper.toDomain
import ir.yar.anbar.data.mapper.toEntity
import ir.yar.anbar.data.mapper.toRequestDto
import ir.yar.anbar.data.remote.datasource.UserProductRemoteDataSource
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSyncResult
import ir.yar.anbar.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val localDataSource: UserProductLocalDataSource,
    private val remoteDataSource: UserProductRemoteDataSource
) : ProductRepository {

    override suspend fun addProduct(product: Product, imageFile: File?) {
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
                imageFile = imageFile
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
        // Local-first read. Server rows are pulled in through addProduct/getProductById
        // sync paths; a full paged pull (getUserProducts) needs conflict resolution and
        // belongs to a dedicated sync pass, since the list DTO lacks local-only fields.
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
                imageFile = product.image?.localUri?.let(::File)
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
                    val merged = serverProduct.mergeInto(entity)
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
                    imageFile = entity.imageLocalPath?.let(::File)
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
                    imageFile = entity.imageLocalPath?.let(::File)
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

}
