package ir.yar.anbar.data.remote.api

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.dto.CatalogProductDto
import ir.yar.anbar.data.remote.dto.response.ResponseDto
import ir.yar.anbar.data.remote.dto.response.PagedResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceMainProduct {

    @GET(ApiConstants.API + ApiConstants.CATALOG + "products")
    suspend fun getAllProducts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PagedResponseDto<CatalogProductDto>>

    @GET(ApiConstants.API + ApiConstants.CATALOG + "products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponseDto<CatalogProductDto>>

    @POST(ApiConstants.API + ApiConstants.ADMIN_CATALOG + "products")
    suspend fun createProduct(@Body product: CatalogProductDto): ApiResponse<ResponseDto<Long>>

    @PUT(ApiConstants.API + ApiConstants.ADMIN_CATALOG + "products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body product: CatalogProductDto
    ): ApiResponse<ResponseDto<Unit>>

    @DELETE(ApiConstants.API + ApiConstants.ADMIN_CATALOG + "products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): ApiResponse<ResponseDto<Unit>>
}