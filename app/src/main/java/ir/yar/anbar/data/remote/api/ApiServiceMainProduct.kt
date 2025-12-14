package ir.yar.anbar.data.remote.api

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.dto.ProductDto
import ir.yar.anbar.data.remote.dto.response.ApiResponseDto
import ir.yar.anbar.data.remote.dto.response.PagedResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceMainProduct {

    @GET(ApiConstants.API + "products")
    suspend fun getAllProducts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PagedResponseDto<ProductDto>>

    @GET(ApiConstants.API +"products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ):  ApiResponse<PagedResponseDto<ProductDto>>

    @POST("products")
    suspend fun createProduct(@Body product: ProductDto): ApiResponse<ApiResponseDto<Long>>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body product: ProductDto
    ): ApiResponse<ApiResponseDto<String>>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): ApiResponse<ApiResponseDto<String>>
}