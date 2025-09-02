package com.example.composetrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.composetrainer.data.local.entity.ProductSalesSummaryEntity

@Dao
interface ProductSalesSummaryDao {

    @Insert
    suspend fun insert(summary: ProductSalesSummaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(summary: ProductSalesSummaryEntity)

    @Query("""
        SELECT * FROM product_sales_summary
        WHERE date BETWEEN :start AND :end
        ORDER BY totalSold DESC
    """)
    suspend fun getTopSellingProductsBetween(
        start: Long,
        end: Long
    ): List<ProductSalesSummaryEntity>

    @Query("""
        SELECT * FROM product_sales_summary
        WHERE productId = :productId AND date = :date
        LIMIT 1
    """)
    suspend fun getByProductAndDate(productId: Long, date: Long): ProductSalesSummaryEntity?

    @Update
    suspend fun update(summary: ProductSalesSummaryEntity)

    @Transaction
    suspend fun insertOrUpdate(summary: ProductSalesSummaryEntity) {
        val existing = getByProductAndDate(summary.productId, summary.date)
        if (existing != null) {
            // Update existing record with accumulated values
            val updated = existing.copy(
                totalSold = existing.totalSold + summary.totalSold,
                totalRevenue = existing.totalRevenue + summary.totalRevenue,
                totalCost = existing.totalCost + summary.totalCost,
                updatedAt = System.currentTimeMillis()
            )
            update(updated)
        } else {
            // Insert new record
            upsert(summary)
        }
    }
}
