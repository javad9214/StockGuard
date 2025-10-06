package com.example.composetrainer.data.repository

import com.example.composetrainer.data.remote.api.ApiServiceMainProduct
import com.example.composetrainer.data.remote.dto.PagedResponseDto
import com.example.composetrainer.data.remote.util.ApiResponseHandler
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.toDomain
import com.example.composetrainer.domain.model.toDto
import com.example.composetrainer.domain.repository.ServerMainProductRepository
import com.example.login.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class ServerMainProductRepoImpl(private val apiServiceMainProduct: ApiServiceMainProduct) :
    ServerMainProductRepository {

    override suspend fun createProduct(product: Product): Flow<Resource<Long>> {
         return ApiResponseHandler.handleApiResponseWithMessage(
             apiCall = {apiServiceMainProduct.createProduct(product.toDto())}
         )
    }

    override suspend fun updateProduct(id: Long, product: Product): Flow<Resource<String>>{
        return ApiResponseHandler.handleApiResponseWithMessage(
            apiCall = { apiServiceMainProduct.updateProduct(id, product.toDto()) }
        )
    }

    override suspend fun deleteProduct(id: Long): Flow<Resource<String>> {
        return ApiResponseHandler.handleApiResponseWithMessage(
            apiCall = { apiServiceMainProduct.deleteProduct(id) }
        )
    }


    override  fun getAllProducts(page: Int, size: Int): Flow<Resource<PagedResponseDto<Product>>> {
        return ApiResponseHandler.handleApiResponse(
            apiCall = { apiServiceMainProduct.getAllProducts(page, size) },
            mapper = { pagedResponseDto ->
                PagedResponseDto(
                    content = pagedResponseDto.content.map { it.toDomain() },
                    page = pagedResponseDto.page,
                    size = pagedResponseDto.size,
                    totalElements = pagedResponseDto.totalElements,
                    totalPages = pagedResponseDto.totalPages,
                    last = pagedResponseDto.last
                )
            }
        )
    }

    override fun searchProducts(
        query: String,
        page: Int,
        size: Int
    ): Flow<Resource<PagedResponseDto<Product>>> {
        return ApiResponseHandler.handleApiResponse(
            apiCall = { apiServiceMainProduct.searchProducts(query, page, size) },
            mapper = { pagedResponseDto ->
                PagedResponseDto(
                    content = pagedResponseDto.content.map { it.toDomain() },
                    page = pagedResponseDto.page,
                    size = pagedResponseDto.size,
                    totalElements = pagedResponseDto.totalElements,
                    totalPages = pagedResponseDto.totalPages,
                    last = pagedResponseDto.last
                )
            }
        )
    }
}