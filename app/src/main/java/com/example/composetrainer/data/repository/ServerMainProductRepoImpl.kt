package com.example.composetrainer.data.repository

import com.example.composetrainer.data.remote.api.ApiServiceMainProduct
import com.example.composetrainer.data.remote.dto.PagedResponseDto
import com.example.composetrainer.data.remote.dto.ProductDto
import com.example.composetrainer.data.remote.util.ApiResponseHandler
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.toDomain
import com.example.composetrainer.domain.repository.ServerMainProductRepository
import com.example.composetrainer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class ServerMainProductRepoImpl(private val apiServiceMainProduct: ApiServiceMainProduct) :
    ServerMainProductRepository {

    override suspend fun createProduct(product: ProductDto): ProductDto {
        return apiServiceMainProduct.createProduct(product)
    }

    override suspend fun updateProduct(id: Long, product: ProductDto): ProductDto {
        return apiServiceMainProduct.updateProduct(id, product)
    }

    override suspend fun deleteProduct(id: Long) {
        apiServiceMainProduct.deleteProduct(id)
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