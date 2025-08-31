package com.example.composetrainer.domain.usecase.product

import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsByIDsUseCase @Inject constructor(
    private val productRepository: ProductRepository){

    suspend operator fun invoke(idProducts: List<Long>): List<Product> {
        return productRepository.getProductsByIds(idProducts)
    }
}