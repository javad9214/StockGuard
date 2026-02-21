package ir.yar.anbar.domain.repository

import ir.yar.anbar.data.remote.dto.response.PagedResponseDto
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ServerMainProductRepository {

    suspend fun deleteProduct(id: Long): Flow<Resource<String>>
    fun getAllProducts(page: Int, size: Int): Flow<Resource<PagedResponseDto<Product>>>
    fun searchProducts(query: String, page: Int, size: Int): Flow<Resource<PagedResponseDto<Product>>>
}