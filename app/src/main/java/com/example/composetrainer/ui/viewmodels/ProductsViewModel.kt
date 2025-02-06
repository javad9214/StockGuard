package com.example.composetrainer.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.usecase.AddProductUseCase
import com.example.composetrainer.domain.usecase.GetProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductUseCase,
    private val addProductUseCase: AddProductUseCase
): ViewModel(){
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _sortOrder = MutableStateFlow(SortOrder.DESCENDING)
    val sortOrder: StateFlow<SortOrder> get() = _sortOrder

    init {
        loadProducts()
    }

    private fun loadProducts(){
        viewModelScope.launch {
            _isLoading.value = true
            getProductsUseCase(sortOrder.value).collectLatest{ products ->
                _products.value = products
                _isLoading.value = false
            }
        }
    }

    fun addProduct(product: Product){
        viewModelScope.launch {
            addProductUseCase(product)
        }
    }

    fun updateSortOrder(newOrder: SortOrder) {
        _sortOrder.value = newOrder
        Log.i(TAG, "updateSortOrder: $newOrder")
        loadProducts()
    }
}

enum class SortOrder {
    ASCENDING, // Oldest first
    DESCENDING // Newest first (default)
}

private const val TAG = "ProductsViewModel"