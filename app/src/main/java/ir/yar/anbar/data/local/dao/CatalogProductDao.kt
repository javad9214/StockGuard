package ir.yar.anbar.data.local.dao

import androidx.room.*
import ir.yar.anbar.data.local.entity.CatalogProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogProductDao {

    @Query("SELECT * FROM catalog_products WHERE isActive = 1 AND status = 'VERIFIED' ORDER BY qualityScore DESC, adoptionCount DESC")
    fun getAllCatalogProducts(): Flow<List<CatalogProductEntity>>

    @Query("SELECT * FROM catalog_products WHERE id = :id AND isActive = 1")
    suspend fun getCatalogProductById(id: Long): CatalogProductEntity?

    @Query("SELECT * FROM catalog_products WHERE barcode = :barcode AND isActive = 1 LIMIT 1")
    suspend fun getCatalogProductByBarcode(barcode: String): CatalogProductEntity?

    @Query("""
        SELECT * FROM catalog_products 
        WHERE isActive = 1 
        AND status = 'VERIFIED'
        AND (name LIKE '%' || :query || '%' 
             OR brand LIKE '%' || :query || '%' 
             OR category LIKE '%' || :query || '%')
        ORDER BY qualityScore DESC
    """)
    fun searchCatalog(query: String): Flow<List<CatalogProductEntity>>

    @Query("SELECT * FROM catalog_products WHERE category = :category AND isActive = 1 AND status = 'VERIFIED' ORDER BY adoptionCount DESC")
    fun getCatalogByCategory(category: String): Flow<List<CatalogProductEntity>>

    @Query("SELECT * FROM catalog_products WHERE isActive = 1 AND status = 'VERIFIED' ORDER BY adoptionCount DESC LIMIT :limit")
    fun getPopularProducts(limit: Int = 20): Flow<List<CatalogProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogProduct(product: CatalogProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogProducts(products: List<CatalogProductEntity>)

    @Update
    suspend fun updateCatalogProduct(product: CatalogProductEntity)

    @Delete
    suspend fun deleteCatalogProduct(product: CatalogProductEntity)

    @Query("DELETE FROM catalog_products WHERE cachedAt < :expireTime")
    suspend fun deleteExpiredCache(expireTime: Long)

    @Query("DELETE FROM catalog_products")
    suspend fun clearAllCache()

    @Query("SELECT COUNT(*) FROM catalog_products WHERE isActive = 1")
    suspend fun getCatalogCount(): Int
}