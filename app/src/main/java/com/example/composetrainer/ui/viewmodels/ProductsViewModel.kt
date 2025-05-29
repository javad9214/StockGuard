package com.example.composetrainer.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.usecase.AddProductUseCase
import com.example.composetrainer.domain.usecase.DecreaseStockUseCase
import com.example.composetrainer.domain.usecase.DeleteProductUseCase
import com.example.composetrainer.domain.usecase.EditProductUseCase
import com.example.composetrainer.domain.usecase.GetProductUseCase
import com.example.composetrainer.domain.usecase.IncreaseStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val editProductUseCase: EditProductUseCase,
    private val increaseStockUseCase: IncreaseStockUseCase,
    private val decreaseStockUseCase: DecreaseStockUseCase
) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _sortOrder = MutableStateFlow(SortOrder.DESCENDING)
    val sortOrder: StateFlow<SortOrder> get() = _sortOrder

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> get() = _selectedProduct

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            getProductsUseCase(sortOrder.value, searchQuery.value).collectLatest { products ->
                _products.value = products
                _isLoading.value = false
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            addProductUseCase(product)
        }
    }

    fun addRandomProducts() {
        viewModelScope.launch {
            repeat(5) {
                val randomProduct = Product(
                    id = 0L,
                    name = "Product " + ('A'..'Z').random() + (100..999).random(),
                    barcode = (100000000000..999999999999).random().toString(),
                    price = listOf(100L, 250L, 499L, 999L, 1299L).random(),
                    image = null,
                    subCategoryId = (1..5).random(),
                    date = System.currentTimeMillis() - (0..1000000).random(),
                    stock = (1..100).random()
                )
                addProductUseCase(randomProduct)
            }
            loadProducts()
        }
    }

    fun updateSortOrder(newOrder: SortOrder) {
        _sortOrder.value = newOrder
        loadProducts()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        loadProducts()
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            deleteProductUseCase(product)
            loadProducts()
        }
    }

    fun editProduct(product: Product) {
        viewModelScope.launch {
            editProductUseCase(product)
            loadProducts()
        }
    }

    // Increase stock
    fun increaseStock(product: Product) {
        viewModelScope.launch {
            increaseStockUseCase(product)
            loadProducts() // Refresh the list
        }
    }

    // Decrease stock
    fun decreaseStock(product: Product) {
        viewModelScope.launch {
            decreaseStockUseCase(product)
            loadProducts() // Refresh the list
        }
    }

    fun getProductById(productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Using the existing getProductsUseCase and filtering by ID
                val allProducts = getProductsUseCase(sortOrder.value, "").first()
                _selectedProduct.value = allProducts.find { it.id == productId }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProductStock(productId: Long, newStock: Int) {
        viewModelScope.launch {
            val product = _selectedProduct.value?.copy(stock = newStock)
            product?.let {
                editProductUseCase(it)
                _selectedProduct.value = it
                loadProducts() // Refresh the list if it's visible
            }
        }
    }

    fun updateProductPrice(productId: Long, newPrice: Long) {
        viewModelScope.launch {
            val product = _selectedProduct.value?.copy(price = newPrice)
            product?.let {
                editProductUseCase(it)
                _selectedProduct.value = it
                loadProducts() // Refresh the list if it's visible
            }
        }
    }
}

enum class SortOrder {
    ASCENDING, // Oldest first
    DESCENDING // Newest first (default)
}

private const val TAG = "ProductsViewModel"
