package ir.yar.anbar.data.repository

import ir.yar.anbar.data.local.dao.ProductSalesSummaryDao
import ir.yar.anbar.data.mapper.toDomain
import ir.yar.anbar.data.mapper.toEntity
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.repository.ProductSalesSummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductSalesSummaryRepoImpl @Inject constructor(
    private val productSalesSummaryDao: ProductSalesSummaryDao
) : ProductSalesSummaryRepository {

    override suspend fun insertProductSale(productSalesSummary: ProductSalesSummary) {
        productSalesSummaryDao.insert(productSalesSummary.toEntity())
    }

    override suspend fun updateProductSale(productSalesSummary: ProductSalesSummary) {
        productSalesSummaryDao.update(productSalesSummary.toEntity())
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
    ): ProductSalesSummary? {
        return productSalesSummaryDao.getByProductAndDate(productId, date)?.toDomain()
    }
}
