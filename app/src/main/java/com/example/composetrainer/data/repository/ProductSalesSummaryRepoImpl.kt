package com.example.composetrainer.data.repository

import android.util.Log
import com.example.composetrainer.data.local.dao.ProductSalesSummaryDao
import com.example.composetrainer.data.local.entity.ProductSalesSummaryEntity
import com.example.composetrainer.domain.model.ProductSalesSummary
import com.example.composetrainer.domain.model.toDomain
import com.example.composetrainer.domain.model.toEntity
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
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