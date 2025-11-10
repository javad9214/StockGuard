package com.example.composetrainer.domain.usecase.analytics

import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLowStockProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    operator fun invoke(stock: Int): Flow<List<Product>> {
        return productRepository.getProductsLowStock(stock)
    }

}