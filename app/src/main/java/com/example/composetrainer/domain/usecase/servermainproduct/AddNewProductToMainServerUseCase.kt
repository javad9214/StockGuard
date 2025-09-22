package com.example.composetrainer.domain.usecase.servermainproduct

import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ServerMainProductRepository
import javax.inject.Inject

class AddNewProductToMainServerUseCase @Inject constructor(
    private val repository: ServerMainProductRepository
) {

    suspend operator fun invoke(product: Product) {
        repository.createProduct(product)
    }
  
}