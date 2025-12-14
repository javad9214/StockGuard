package ir.yar.anbar.data.repository

import ir.yar.anbar.data.local.dao.ProductDao
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.toDomain
import ir.yar.anbar.domain.model.toEntity
import ir.yar.anbar.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {

    override suspend fun addProduct(product: Product) {
        val entity = product.toEntity()
        productDao.insertProduct(entity)
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
            .map { entityList ->
                entityList.map { entity ->
                    entity.toDomain()
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

    override suspend fun updateProduct(product: Product) : Int{
       return productDao.updateProduct(product.toEntity())
    }

    override suspend fun getProductById(id: Long): Product? {
        val entity = productDao.getProductById(id)
        return entity?.toDomain()
    }

    override suspend fun getProductsByIds(ids: List<Long>): List<Product> {
        return productDao.getProductsByIds(productIds = ids).map { it.toDomain() }
    }

    override fun getProductsLowStock(stockLimit: Int): Flow<List<Product>> {
        return productDao.getProductsByStock(stockLimit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

}