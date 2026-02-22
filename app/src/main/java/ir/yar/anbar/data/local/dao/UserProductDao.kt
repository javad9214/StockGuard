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
    suspend fun insertProduct(userProductEntity: UserProductEntity)

    @Query("SELECT * FROM user_products")
    fun getAllProducts(): Flow<List<UserProductEntity>>

    @Query("SELECT * FROM user_products WHERE name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<UserProductEntity>>

    @Delete
    suspend fun deleteProduct(product: UserProductEntity)

    @Update
    suspend fun updateProduct(product: UserProductEntity): Int

    @Query("SELECT * FROM user_products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): UserProductEntity?

    @Query("SELECT * FROM user_products WHERE id IN (:productIds)")
    suspend fun getProductsByIds(productIds: List<Long>): List<UserProductEntity>

    @Query("SELECT * FROM user_products WHERE stock <= :inputStock ORDER BY stock ASC")
    fun getProductsByStock(inputStock: Int): Flow<List<UserProductEntity>>

}