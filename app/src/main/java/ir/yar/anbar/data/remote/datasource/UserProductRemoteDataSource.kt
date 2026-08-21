package ir.yar.anbar.data.remote.datasource

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.api.ApiServiceUserProduct
import ir.yar.anbar.data.remote.dto.request.UserProductRequestDto
import ir.yar.anbar.data.remote.dto.response.ApiResponseDto
import ir.yar.anbar.data.remote.dto.response.PagedResponseDto
import ir.yar.anbar.data.remote.dto.response.UserProductResponseDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

/**
 * Remote data source for user product operations.
 * Wraps [ApiServiceUserProduct] and owns the multipart image-part
 * construction so callers only deal with plain files.
 */
class UserProductRemoteDataSource @Inject constructor(
    private val apiService: ApiServiceUserProduct
) {

    /** Get all products for the current user (paginated) */
    suspend fun getUserProducts(
        page: Int,
        size: Int
    ): ApiResponse<PagedResponseDto<UserProductResponseDto>> =
        apiService.getUserProducts(page, size)

    /** Get a single product by its server ID */
    suspend fun getProductById(id: Long): ApiResponse<UserProductResponseDto> =
        apiService.getProductById(id)

    /** Create a custom product (not from catalog); returns the new server ID */
    suspend fun createCustomProduct(
        product: UserProductRequestDto,
        imageFile: File?
    ): ApiResponse<ApiResponseDto<Long>> =
        apiService.createCustomProduct(product, imageFile.toImagePart())

    /** Adopt a catalog product into the user's inventory; returns the new server ID */
    suspend fun adoptCatalogProduct(
        catalogProductId: Long,
        product: UserProductRequestDto,
        imageFile: File?
    ): ApiResponse<ApiResponseDto<Long>> =
        apiService.adoptCatalogProduct(catalogProductId, product, imageFile.toImagePart())

    /** Update an existing product on the server */
    suspend fun updateProduct(
        id: Long,
        product: UserProductRequestDto,
        imageFile: File?
    ): ApiResponse<ApiResponseDto<Unit>> =
        apiService.updateProduct(id, product, imageFile.toImagePart())

    /** Delete a product on the server (soft delete) */
    suspend fun deleteProduct(id: Long): ApiResponse<ApiResponseDto<Unit>> =
        apiService.deleteProduct(id)

    /** Search the user's products (paginated) */
    suspend fun searchProducts(
        query: String,
        page: Int = 0,
        size: Int = 20
    ): ApiResponse<PagedResponseDto<UserProductResponseDto>> =
        apiService.searchProducts(query, page, size)

    private fun File?.toImagePart(): MultipartBody.Part? =
        this?.takeIf { it.exists() }?.let { file ->
            MultipartBody.Part.createFormData(
                name = "image",
                filename = file.name,
                body = file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }
}
