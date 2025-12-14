package ir.yar.anbar.data.repository

import ir.yar.anbar.data.remote.api.ApiServiceMainProduct
import ir.yar.anbar.data.remote.dto.response.PagedResponseDto
import ir.yar.anbar.data.remote.util.ApiResponseHandler
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.toDomain
import ir.yar.anbar.domain.model.toDto
import ir.yar.anbar.domain.repository.ServerMainProductRepository
import ir.yar.anbar.domain.util.Resource

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