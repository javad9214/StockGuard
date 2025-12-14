package ir.yar.anbar.data.local.dao

import androidx.room.*
import ir.yar.anbar.data.local.entity.ProductSalesSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductSalesSummaryDao {

    @Insert
    suspend fun insert(summary: ProductSalesSummaryEntity)

    @Query(
        """
        SELECT * FROM product_sales_summary
        WHERE date BETWEEN :start AND :end
        ORDER BY totalSold DESC
    """
    )
    fun getTopSellingProductsBetween(
        start: Long,
        end: Long
    ): Flow<List<ProductSalesSummaryEntity>>


    @Query(
        """
        SELECT * FROM product_sales_summary
        WHERE date BETWEEN :start AND :end
        ORDER BY (totalRevenue - totalCost) DESC
    """)
    fun getTopProfitableProductsBetween(
        start: Long,
        end: Long
    ): Flow<List<ProductSalesSummaryEntity>>


    @Query(
        """
        SELECT * FROM product_sales_summary
        WHERE productId = :productId AND date = :date
        LIMIT 1
    """
    )
    suspend fun getByProductAndDate(productId: Long, date: Long): ProductSalesSummaryEntity?

    @Update
    suspend fun update(summary: ProductSalesSummaryEntity)
}
