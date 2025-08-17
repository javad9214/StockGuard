package com.example.composetrainer.domain.usecase.product

import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ProductRepository
import com.example.composetrainer.ui.viewmodels.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllProductUseCase @Inject constructor(
    private val repository: ProductRepository
)  {
    operator fun invoke(sortOrder: SortOrder): Flow<List<Product>> {
        return repository.getAllProducts().map { products ->
            when (sortOrder) {
                SortOrder.ASCENDING -> products.sortedBy { it.date }
                SortOrder.DESCENDING -> products.sortedByDescending { it.date }
            }
        }
    }
}