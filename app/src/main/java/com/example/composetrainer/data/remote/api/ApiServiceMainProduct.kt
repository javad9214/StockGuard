package com.example.composetrainer.data.remote.api

import com.example.composetrainer.data.remote.dto.PagedResponseDto
import com.example.composetrainer.data.remote.dto.ProductDto
import retrofit2.http.GET
import com.skydoves.sandwich.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @GET("products/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ):  ApiResponse<PagedResponseDto<ProductDto>>

    @POST("products")
    suspend fun createProduct(@Body product: ProductDto): ProductDto

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body product: ProductDto
    ): ProductDto

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Void>
}