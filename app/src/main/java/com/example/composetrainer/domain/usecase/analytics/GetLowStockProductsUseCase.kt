package com.example.composetrainer.domain.usecase.analytics

import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLowStockProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    operator fun invoke(stock: Int): Flow<List<Product>> {
        return productRepository.getProductsLowStock(stock)
            .map { products ->
                // Partition into two lists: active and dead stock
                val (activeStock, deadStock) = products.partition { !it.isDeadStock() }

                // Return active stock first, then dead stock
                activeStock + deadStock
            }
    }

}