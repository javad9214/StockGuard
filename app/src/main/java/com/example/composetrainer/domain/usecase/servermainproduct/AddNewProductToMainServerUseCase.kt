package com.example.composetrainer.domain.usecase.servermainproduct

import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ServerMainProductRepository
import com.example.composetrainer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddNewProductToMainServerUseCase @Inject constructor(
    private val repository: ServerMainProductRepository
) {

    suspend operator fun invoke(product: Product): Flow<Resource<Long>> {
        return repository.createProduct(product)
    }

}