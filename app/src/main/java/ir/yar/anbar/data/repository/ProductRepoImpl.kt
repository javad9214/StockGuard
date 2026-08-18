package ir.yar.anbar.data.repository

import ir.yar.anbar.data.local.dao.UserProductDao
import ir.yar.anbar.data.mapper.toDomain
import ir.yar.anbar.data.mapper.toEntity
import ir.yar.anbar.data.remote.api.ApiServiceUserProduct
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val userProductDao: UserProductDao,
    private val apiServiceUserProduct: ApiServiceUserProduct
) : ProductRepository {

    override suspend fun addProduct(product: Product, imageFile: File?) {

        // 1. save locally first (offline-first)
        val localId = userProductDao.insertProduct(product.toEntity())

        // 2. build multipart image part if present
        val imagePart = imageFile?.let {
            val requestBody = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", it.name, requestBody)
        }

//        // 3. push to server
//        when (val response = apiServiceUserProduct.createCustomProduct(
//            product = product.toRequestDto(),
//            image = imagePart
//        )) {
//            is ApiResponse.Success -> {
//                val serverId = response.data.data
//                userProductDao.updateProduct(
//                    product.copy(id = ProductId(localId)).toEntity()
//                        .copy(serverId = serverId, synced = true)
//                )
//            }
//            is ApiResponse.Failure -> {
//                // keep local row, mark unsynced — retry later
//            }
//        }

    }

    override fun getAllProducts(): Flow<List<Product>> {
        return userProductDao.getAllProducts()
            .map { entityList ->
                entityList.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        return userProductDao.searchProducts(query)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun deleteProduct(product: Product) {
        userProductDao.deleteProduct(product.toEntity())
    }

    override suspend fun editProduct(product: Product) {
        userProductDao.updateProduct(product.toEntity())
    }

    override suspend fun updateProduct(product: Product) : Int{
       return userProductDao.updateProduct(product.toEntity())
    }

    override suspend fun getProductById(id: Long): Product? {
        val entity = userProductDao.getProductById(id)
        return entity?.toDomain()
    }

    override suspend fun getProductsByIds(ids: List<Long>): List<Product> {
        return userProductDao.getProductsByIds(productIds = ids).map { it.toDomain() }
    }

    override fun getProductsLowStock(stockLimit: Int): Flow<List<Product>> {
        return userProductDao.getProductsByStock(stockLimit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

}