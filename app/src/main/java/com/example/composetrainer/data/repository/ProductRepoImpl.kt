package com.example.composetrainer.data.repository

import com.example.composetrainer.data.local.dao.ProductDao
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.toDomain
import com.example.composetrainer.domain.model.toEntity
import com.example.composetrainer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val productDao: ProductDao
): ProductRepository {

    override suspend fun addProduct(product: Product) {
        val entity = product.toEntity()
        productDao.insertProduct(entity)
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
            .map { entityList ->
                entityList.map {
                    entity -> entity.toDomain()
                }
            }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }

    override suspend fun editProduct(product: Product) {
        productDao.updateProduct(product.toEntity())
    }

    override suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.toEntity())
    }

    override suspend fun getProductById(id: Long): Product? {
        val entity = productDao.getProductById(id)
        return entity?.toDomain()
    }

    override suspend fun getProductsByIds(ids: List<Long>): List<Product> {
        return productDao.getProductsByIds(productIds = ids).map { it.toDomain() }
    }

}