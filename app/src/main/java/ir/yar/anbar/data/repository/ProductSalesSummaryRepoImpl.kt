package ir.yar.anbar.data.repository

import android.util.Log
import ir.yar.anbar.data.local.dao.ProductSalesSummaryDao
import ir.yar.anbar.data.local.entity.ProductSalesSummaryEntity
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.model.toDomain
import ir.yar.anbar.domain.model.toEntity
import ir.yar.anbar.domain.repository.ProductSalesSummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductSalesSummaryRepoImpl @Inject constructor(
    private val productSalesSummaryDao: ProductSalesSummaryDao
) : ProductSalesSummaryRepository {

    val TAG = "ProductSalesSummaryRepoImpl"

    override suspend fun insertProductSale(productSalesSummary: ProductSalesSummary) {
        productSalesSummaryDao.insert(productSalesSummary.toEntity())
        Log.i(TAG, "insertProductSale: ${productSalesSummary.totalCost}")
    }

    override suspend fun updateProductSale(productSalesSummary: ProductSalesSummary) {
        productSalesSummaryDao.update(productSalesSummary.toEntity())
        Log.i(TAG, "updateProductSale: ${productSalesSummary.totalCost}")
    }

    override fun getTopSellingProductsBetween(
        start: Long,
        end: Long
    ): Flow<List<ProductSalesSummary>> {
        return productSalesSummaryDao.getTopSellingProductsBetween(start, end)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getTopProfitableProductsBetween(
        start: Long,
        end: Long
    ): Flow<List<ProductSalesSummary>> {
        return productSalesSummaryDao.getTopProfitableProductsBetween(start, end)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getByProductAndDate(
        productId: Long,
        date: Long
    ): ProductSalesSummaryEntity? {
        return productSalesSummaryDao.getByProductAndDate(productId, date)
    }
}