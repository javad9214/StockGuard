package com.example.composetrainer.domain.usecase.product

import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ProductRepository
import com.example.composetrainer.ui.viewmodels.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductByQueryUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(sortOrder: SortOrder,searchQuery: String): Flow<List<Product>>{
        return repository.searchProducts(searchQuery).map { products ->
            when (sortOrder) {
                SortOrder.ASCENDING -> products.sortedBy { it.date }
                SortOrder.DESCENDING -> products.sortedByDescending { it.date }
            }
        }
    }
}