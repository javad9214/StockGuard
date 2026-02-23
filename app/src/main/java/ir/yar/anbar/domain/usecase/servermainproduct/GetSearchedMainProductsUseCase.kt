package ir.yar.anbar.domain.usecase.servermainproduct

import ir.yar.anbar.data.remote.dto.response.PagedResponseDto
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.repository.ServerMainProductRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchedMainProductsUseCase  @Inject constructor(
    private val repository: ServerMainProductRepository
) {

    operator fun invoke(query: String ,page: Int, size: Int = 20): Flow<Resource<PagedResponseDto<Product>>> {
        return repository.searchProducts(query, page, size)
    }

}