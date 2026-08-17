package ir.yar.anbar.data.remote.api

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.dto.request.UserProductRequestDto
import ir.yar.anbar.data.remote.dto.response.ApiResponseDto
import ir.yar.anbar.data.remote.dto.response.PagedResponseDto
import ir.yar.anbar.data.remote.dto.response.UserProductResponseDto
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceUserProduct {

    @GET("api/products")
    suspend fun getUserProducts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PagedResponseDto<UserProductResponseDto>>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): ApiResponse<UserProductResponseDto>

    @Multipart
    @POST("api/products")
    suspend fun createCustomProduct(
        @Part("product") product: UserProductRequestDto,
        @Part image: MultipartBody.Part?
    ): ApiResponse<ApiResponseDto<Long>>

    @Multipart
    @POST("api/products/adopt/{catalogProductId}")
    suspend fun adoptCatalogProduct(
        @Path("catalogProductId") catalogProductId: Long,
        @Part("product") product: UserProductRequestDto,
        @Part image: MultipartBody.Part?
    ): ApiResponse<ApiResponseDto<Long>>

    @Multipart
    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Part("product") product: UserProductRequestDto,
        @Part image: MultipartBody.Part?
    ): ApiResponse<ApiResponseDto<Unit>>

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): ApiResponse<ApiResponseDto<Unit>>

    @GET("api/products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponseDto<UserProductResponseDto>>
}