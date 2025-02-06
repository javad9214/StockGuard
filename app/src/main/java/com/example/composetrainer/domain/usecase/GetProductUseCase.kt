package com.example.composetrainer.domain.usecase

import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.ProductRepository
import com.example.composetrainer.ui.viewmodels.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(sortOrder: SortOrder,searchQuery: String): Flow<List<Product>>{
        return repository.getAllProducts().map { products ->
            val filteredProducts = if (searchQuery.isNotBlank()) {
                products.filter { product ->
                    product.name.contains(searchQuery, ignoreCase = true) ||
                            product.barcode?.contains(searchQuery, ignoreCase = true) == true
                }
            } else {
                products
            }
            when(sortOrder){
                SortOrder.ASCENDING -> filteredProducts.sortedBy { it.date }
                SortOrder.DESCENDING -> filteredProducts.sortedByDescending { it.date }
            }
        }
    }
}