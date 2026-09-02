package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.ProductSalesSummary
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

    /**
     * @param date day key as epoch millis at local midnight — the format the
     * date column stores
     */
    suspend fun getByProductAndDate(productId: Long, date: Long): ProductSalesSummary?
}
