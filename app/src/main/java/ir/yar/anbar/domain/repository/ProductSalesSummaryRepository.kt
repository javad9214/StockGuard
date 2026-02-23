package ir.yar.anbar.domain.repository

import ir.yar.anbar.data.local.entity.ProductSalesSummaryEntity
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.model.analyze.DailySalesData
import kotlinx.coroutines.flow.Flow

interface ProductSalesSummaryRepository {
    suspend fun insertProductSale(productSalesSummary: ProductSalesSummary)
    suspend fun updateProductSale(productSalesSummary: ProductSalesSummary)

    fun getTopSellingProductsBetween(
        start: Long,
        end: Long
    ): Flow<List<ProductSalesSummary>>

    fun getTopProfitableProductsBetween(
        start: Long,
        end: Long
    ): Flow<List<ProductSalesSummary>>

    suspend fun getByProductAndDate(productId: Long, date: Long): ProductSalesSummaryEntity?

    fun getDailySalesBetween(startDate: Long, endDate: Long): Flow<List<DailySalesData>>
}