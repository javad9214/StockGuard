package ir.yar.anbar.data.repository

import ir.yar.anbar.data.mapper.toDomain
import ir.yar.anbar.data.remote.api.ApiServiceMainProduct
import ir.yar.anbar.data.remote.dto.CatalogProductDto
import ir.yar.anbar.data.remote.dto.response.PagedResponseDto
import ir.yar.anbar.data.remote.util.ApiResponseHandler
import ir.yar.anbar.domain.model.PagedResult
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.repository.ServerMainProductRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class ServerMainProductRepoImpl(private val apiServiceMainProduct: ApiServiceMainProduct) :
    ServerMainProductRepository {

    override fun getAllProducts(page: Int, size: Int): Flow<Resource<PagedResult<Product>>> {
        return ApiResponseHandler.handleApiResponse(
            apiCall = { apiServiceMainProduct.getAllProducts(page, size) },
            mapper = { it.toPagedResult() }
        )
    }

    override fun searchProducts(
        query: String,
        page: Int,
        size: Int
    ): Flow<Resource<PagedResult<Product>>> {
        return ApiResponseHandler.handleApiResponse(
            apiCall = { apiServiceMainProduct.searchProducts(query, page, size) },
            mapper = { it.toPagedResult() }
        )
    }

    private fun PagedResponseDto<CatalogProductDto>.toPagedResult(): PagedResult<Product> =
        PagedResult(
            content = content.map { it.toDomain() },
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
            last = last
        )
}
