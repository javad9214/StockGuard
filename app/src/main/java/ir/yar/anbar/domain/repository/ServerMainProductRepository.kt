package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.PagedResult
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ServerMainProductRepository {

    fun getAllProducts(page: Int, size: Int): Flow<Resource<PagedResult<Product>>>

    fun searchProducts(query: String, page: Int, size: Int): Flow<Resource<PagedResult<Product>>>
}
