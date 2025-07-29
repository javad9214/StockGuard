package com.example.composetrainer.data.repository

import com.example.composetrainer.data.local.dao.ProductDao
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

    override fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query)
            .map { entities -> entities.map { ProductMapper.toDomain(it) } }
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(ProductMapper.toEntity(product))
    }

    override suspend fun editProduct(product: Product) {
        productDao.updateProduct(ProductMapper.toEntity(product))
    }

    override suspend fun updateProduct(product: Product) {
        productDao.updateProduct(ProductMapper.toEntity(product))
    }

    override suspend fun getProductById(id: Long): Product? {
        val entity = productDao.getProductById(id)
        return entity?.let { ProductMapper.toDomain(it) }
    }

}