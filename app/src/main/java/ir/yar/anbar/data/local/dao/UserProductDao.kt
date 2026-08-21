package ir.yar.anbar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.yar.anbar.data.local.entity.UserProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProductDao {

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertProduct(userProductEntity: UserProductEntity): Long

    @Query("UPDATE user_products SET serverId = :serverId, synced = 1, syncStatus = 'SYNCED', updatedAt = :updatedAt WHERE id = :localId")
    suspend fun markProductSynced(localId: Long, serverId: Long, updatedAt: Long)

    @Query("SELECT * FROM user_products WHERE isDeleted = 0")
    fun getAllProducts(): Flow<List<UserProductEntity>>

    @Query("SELECT * FROM user_products WHERE isDeleted = 0 AND (name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%')")
    fun searchProducts(query: String): Flow<List<UserProductEntity>>

    @Delete
    suspend fun deleteProduct(product: UserProductEntity)

    @Update
    suspend fun updateProduct(product: UserProductEntity): Int

    @Query("UPDATE user_products SET syncStatus = 'PENDING_DELETE', synced = 0, isDeleted = 1, updatedAt = :updatedAt WHERE id = :localId")
    suspend fun markProductPendingDelete(localId: Long, updatedAt: Long)

    @Query("SELECT * FROM user_products WHERE id = :id AND isDeleted = 0 LIMIT 1")
    suspend fun getProductById(id: Long): UserProductEntity?

    @Query("SELECT * FROM user_products WHERE isDeleted = 0 AND id IN (:productIds)")
    suspend fun getProductsByIds(productIds: List<Long>): List<UserProductEntity>

    @Query("SELECT * FROM user_products WHERE isDeleted = 0 AND stock <= :inputStock ORDER BY stock ASC")
    fun getProductsByStock(inputStock: Int): Flow<List<UserProductEntity>>

    @Query("SELECT * FROM user_products WHERE isActive = 1 AND isDeleted = 0")
    fun getActiveProducts(): Flow<List<UserProductEntity>>

    @Query("SELECT * FROM user_products WHERE serverId = :serverId AND isDeleted = 0 LIMIT 1")
    suspend fun getProductByServerId(serverId: Long): UserProductEntity?

    @Query("SELECT * FROM user_products WHERE catalogProductId = :catalogProductId AND isDeleted = 0 LIMIT 1")
    suspend fun getProductByCatalogId(catalogProductId: Long): UserProductEntity?

    @Query("SELECT * FROM user_products WHERE isDeleted = 0 AND syncStatus != 'SYNCED' ORDER BY updatedAt ASC")
    suspend fun getPendingSyncProducts(): List<UserProductEntity>

    @Query("SELECT * FROM user_products WHERE syncStatus = 'PENDING_CREATE' AND isDeleted = 0 ORDER BY createdAt ASC")
    suspend fun getPendingCreateProducts(): List<UserProductEntity>

    @Query("SELECT * FROM user_products WHERE syncStatus = 'PENDING_UPDATE' AND isDeleted = 0 ORDER BY updatedAt ASC")
    suspend fun getPendingUpdateProducts(): List<UserProductEntity>

    @Query("SELECT * FROM user_products WHERE syncStatus = 'PENDING_DELETE' AND isDeleted = 1 ORDER BY updatedAt ASC")
    suspend fun getPendingDeleteProducts(): List<UserProductEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<UserProductEntity>)

    @Query("UPDATE user_products SET syncStatus = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, updatedAt: Long)

    @Query(
        "UPDATE user_products SET stock = stock + :quantity, synced = 0, " +
            "syncStatus = CASE WHEN syncStatus = 'PENDING_CREATE' THEN 'PENDING_CREATE' ELSE 'PENDING_UPDATE' END, " +
            "updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun incrementStock(id: Long, quantity: Int, updatedAt: Long)

    @Query(
        "UPDATE user_products SET stock = MAX(stock - :quantity, 0), synced = 0, " +
            "syncStatus = CASE WHEN syncStatus = 'PENDING_CREATE' THEN 'PENDING_CREATE' ELSE 'PENDING_UPDATE' END, " +
            "updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun decrementStock(id: Long, quantity: Int, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM user_products WHERE isDeleted = 0")
    suspend fun getUserProductCount(): Int

    @Query("SELECT COUNT(*) FROM user_products WHERE catalogProductId IS NOT NULL AND isDeleted = 0")
    suspend fun getAdoptedProductCount(): Int

    @Query("SELECT COUNT(*) FROM user_products WHERE catalogProductId IS NULL AND isDeleted = 0")
    suspend fun getCustomProductCount(): Int

    @Query("DELETE FROM user_products WHERE isDeleted = 1 AND updatedAt < :threshold")
    suspend fun hardDeleteOldSoftDeletedProducts(threshold: Long)

}