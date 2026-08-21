package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSyncResult
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ProductRepository {

    suspend fun addProduct(product: Product, imageFile: File?)

    /**
     * Pushes every locally pending product (PENDING_CREATE, PENDING_UPDATE,
     * PENDING_DELETE) to the server and returns the outcome.
     */
    suspend fun syncAllProducts(): ProductSyncResult

    fun getAllProducts(): Flow<List<Product>>

    fun searchProducts(query: String): Flow<List<Product>>

    suspend fun deleteProduct(product: Product)

    suspend fun editProduct(product: Product)

    suspend fun updateProduct(product: Product): Int

    suspend fun getProductById(id: Long): Product?

    suspend fun getProductsByIds(ids: List<Long>): List<Product>

    fun getProductsLowStock(stockLimit: Int): Flow<List<Product>>
}