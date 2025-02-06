package com.example.composetrainer.data.repository

import com.example.composetrainer.data.local.dao.ProductDao
import com.example.composetrainer.data.mapper.ProductMapper
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val productDao: ProductDao
): ProductRepository {

    override suspend fun addProduct(product: Product) {
        val entity = ProductMapper.toEntity(product)
        productDao.insertProduct(entity)
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
            .map { entityList ->
                entityList.map {
                    entity -> ProductMapper.toDomain(entity)
                }
            }
    }

}