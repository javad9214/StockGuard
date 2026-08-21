package ir.yar.anbar.data.local.datasource

import ir.yar.anbar.data.local.dao.UserProductDao
import ir.yar.anbar.data.local.entity.UserProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Local data source for user product operations (Room).
 * Thin wrapper around [UserProductDao] that owns timestamps for
 * status/stock mutations so callers can't forget to set them.
 */
class UserProductLocalDataSource @Inject constructor(
    private val dao: UserProductDao
) {

    //region Reads

    fun observeAllProducts(): Flow<List<UserProductEntity>> = dao.getAllProducts()

    fun searchProducts(query: String): Flow<List<UserProductEntity>> = dao.searchProducts(query)

    fun observeLowStockProducts(stockLimit: Int): Flow<List<UserProductEntity>> =
        dao.getProductsByStock(stockLimit)

    fun observeActiveProducts(): Flow<List<UserProductEntity>> = dao.getActiveProducts()

    suspend fun getProductById(id: Long): UserProductEntity? = dao.getProductById(id)

    suspend fun getProductByServerId(serverId: Long): UserProductEntity? =
        dao.getProductByServerId(serverId)

    suspend fun getProductByCatalogId(catalogProductId: Long): UserProductEntity? =
        dao.getProductByCatalogId(catalogProductId)

    suspend fun getProductsByIds(ids: List<Long>): List<UserProductEntity> =
        dao.getProductsByIds(ids)

    //endregion

    //region Sync-pending reads

    suspend fun getPendingSyncProducts(): List<UserProductEntity> = dao.getPendingSyncProducts()

    suspend fun getPendingCreateProducts(): List<UserProductEntity> = dao.getPendingCreateProducts()

    suspend fun getPendingUpdateProducts(): List<UserProductEntity> = dao.getPendingUpdateProducts()

    suspend fun getPendingDeleteProducts(): List<UserProductEntity> = dao.getPendingDeleteProducts()

    //endregion

    //region Writes

    suspend fun insertProduct(product: UserProductEntity): Long = dao.insertProduct(product)

    suspend fun insertProducts(products: List<UserProductEntity>) = dao.insertProducts(products)

    suspend fun updateProduct(product: UserProductEntity): Int = dao.updateProduct(product)

    suspend fun deleteProduct(product: UserProductEntity) = dao.deleteProduct(product)

    suspend fun markProductSynced(localId: Long, serverId: Long) =
        dao.markProductSynced(localId, serverId, System.currentTimeMillis())

    suspend fun markProductPendingDelete(id: Long) =
        dao.markProductPendingDelete(id, System.currentTimeMillis())

    suspend fun updateSyncStatus(id: Long, status: String) =
        dao.updateSyncStatus(id, status, System.currentTimeMillis())

    //endregion

    //region Atomic stock updates

    suspend fun incrementStock(id: Long, quantity: Int) =
        dao.incrementStock(id, quantity, System.currentTimeMillis())

    suspend fun decrementStock(id: Long, quantity: Int) =
        dao.decrementStock(id, quantity, System.currentTimeMillis())

    //endregion

    //region Counts & cleanup

    suspend fun getProductCount(): Int = dao.getUserProductCount()

    suspend fun getAdoptedProductCount(): Int = dao.getAdoptedProductCount()

    suspend fun getCustomProductCount(): Int = dao.getCustomProductCount()

    suspend fun hardDeleteOldSoftDeletedProducts(threshold: Long) =
        dao.hardDeleteOldSoftDeletedProducts(threshold)

    //endregion
}
