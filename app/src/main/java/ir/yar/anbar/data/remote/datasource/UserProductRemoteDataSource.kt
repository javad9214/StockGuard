package ir.yar.anbar.data.remote.datasource

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.api.ApiServiceUserProduct
import ir.yar.anbar.data.remote.dto.request.UserProductRequestDto
import ir.yar.anbar.data.remote.dto.response.ApiResponseDto
import ir.yar.anbar.data.remote.dto.response.PagedResponseDto
import ir.yar.anbar.data.remote.dto.response.UserProductResponseDto
import ir.yar.anbar.data.util.ProductImageFileManager
import javax.inject.Inject

/**
 * Remote data source for user product operations.
 * Wraps [ApiServiceUserProduct] and delegates the multipart image-part
 * construction to [ProductImageFileManager] so callers only pass the image
 * reference exactly as it is stored on the product (content:// URI or path).
 */
class UserProductRemoteDataSource @Inject constructor(
    private val apiService: ApiServiceUserProduct,
    private val imageFileManager: ProductImageFileManager
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
        imageSource: String?
    ): ApiResponse<ApiResponseDto<Long>> =
        apiService.createCustomProduct(product, imageFileManager.createUploadPart(imageSource))

    /** Adopt a catalog product into the user's inventory; returns the new server ID */
    suspend fun adoptCatalogProduct(
        catalogProductId: Long,
        product: UserProductRequestDto,
        imageSource: String?
    ): ApiResponse<ApiResponseDto<Long>> =
        apiService.adoptCatalogProduct(
            catalogProductId,
            product,
            imageFileManager.createUploadPart(imageSource)
        )

    /** Update an existing product on the server */
    suspend fun updateProduct(
        id: Long,
        product: UserProductRequestDto,
        imageSource: String?
    ): ApiResponse<ApiResponseDto<Unit>> =
        apiService.updateProduct(id, product, imageFileManager.createUploadPart(imageSource))

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
}
