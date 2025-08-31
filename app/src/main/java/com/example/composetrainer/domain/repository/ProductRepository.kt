package com.example.composetrainer.domain.repository

import com.example.composetrainer.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    suspend fun addProduct(product: Product)

    fun getAllProducts(): Flow<List<Product>>

    fun searchProducts(query: String): Flow<List<Product>>

    suspend fun deleteProduct(product: Product)

    suspend fun editProduct(product: Product)

    suspend fun updateProduct(product: Product)

    suspend fun getProductById(id: Long): Product?

    suspend fun getProductsByIds(ids: List<Long>): List<Product>
}