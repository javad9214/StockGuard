package com.example.composetrainer.data.local.dao

import androidx.room.*
import com.example.composetrainer.data.local.entity.ProductSalesSummaryEntity

@Dao
interface ProductSalesSummaryDao {

    @Insert
    suspend fun insert(summary: ProductSalesSummaryEntity)

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
}
