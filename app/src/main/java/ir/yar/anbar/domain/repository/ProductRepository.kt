package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    suspend fun addProduct(product: Product)

    fun getAllProducts(): Flow<List<Product>>

    fun searchProducts(query: String): Flow<List<Product>>

    suspend fun deleteProduct(product: Product)

    suspend fun editProduct(product: Product)

    suspend fun updateProduct(product: Product): Int

    suspend fun getProductById(id: Long): Product?

    suspend fun getProductsByIds(ids: List<Long>): List<Product>

    fun getProductsLowStock(stockLimit: Int): Flow<List<Product>>
}