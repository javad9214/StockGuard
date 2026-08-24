package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSyncResult
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    /**
     * @param imageSource image reference exactly as stored on the product
     * (content:// picker URI or file path); uploaded with the product
     */
    suspend fun addProduct(product: Product, imageSource: String?)

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