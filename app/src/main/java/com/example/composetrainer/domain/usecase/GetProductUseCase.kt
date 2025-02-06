package com.example.composetrainer.domain.usecase

import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>>{
        return repository.getAllProducts()
    }
}