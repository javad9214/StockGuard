package com.example.composetrainer.domain.usecase.servermainproduct

import com.example.composetrainer.data.remote.dto.PagedResponseDto
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ServerMainProductRepository
import com.example.composetrainer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMainProductsUseCase @Inject constructor(
    private val repository: ServerMainProductRepository
) {

    operator fun invoke(page: Int, size: Int = 20): Flow<Resource<PagedResponseDto<Product>>> {
        return repository.getAllProducts(page, size)
    }

}