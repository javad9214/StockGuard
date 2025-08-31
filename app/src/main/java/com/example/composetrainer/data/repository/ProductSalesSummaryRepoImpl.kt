package com.example.composetrainer.data.repository

import com.example.composetrainer.data.local.dao.ProductSalesSummaryDao
import com.example.composetrainer.data.local.entity.ProductSalesSummaryEntity
import com.example.composetrainer.domain.model.ProductSalesSummary
import com.example.composetrainer.domain.model.toDomain
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getStartOfCurrentHour

import javax.inject.Inject

class ProductSalesSummaryRepoImpl @Inject constructor(
    private val productSalesSummaryDao: ProductSalesSummaryDao
) : ProductSalesSummaryRepository {

    override suspend fun addProductSale(productId: Long, quantity: Int) {
        val currentDate = getStartOfCurrentHour()
        val existingSummary = productSalesSummaryDao.getByProductAndDate(productId, currentDate)

        if (existingSummary != null) {
            val updatedSummary = existingSummary.copy(
                totalSold = existingSummary.totalSold + quantity
            )
            productSalesSummaryDao.update(updatedSummary)
        } else {
            val newSummary = ProductSalesSummaryEntity(
                productId = productId,
                date = currentDate,
                totalSold = quantity
            )
            productSalesSummaryDao.insert(newSummary)
        }
    }

    override suspend fun getTopSellingProductsBetween(
        start: Long,
        end: Long
    ): List<ProductSalesSummary> {
        return productSalesSummaryDao.getTopSellingProductsBetween(start, end).map { it.toDomain() }
    }

    override suspend fun getByProductAndDate(
        productId: Long,
        date: Long
    ): ProductSalesSummaryEntity? {
        return productSalesSummaryDao.getByProductAndDate(productId, date)
    }
}