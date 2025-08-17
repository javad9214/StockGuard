package com.example.composetrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.StockQuantity
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.domain.usecase.product.AddProductUseCase
import com.example.composetrainer.domain.usecase.product.DecreaseStockUseCase
import com.example.composetrainer.domain.usecase.product.DeleteProductUseCase
import com.example.composetrainer.domain.usecase.product.EditProductUseCase
import com.example.composetrainer.domain.usecase.product.GetAllProductUseCase
import com.example.composetrainer.domain.usecase.product.GetProductByQueryUseCase
import com.example.composetrainer.domain.usecase.product.IncreaseStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductByQueryUseCase,
    private val getAllProductUseCase: GetAllProductUseCase,
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage


    private val _sortOrder = MutableStateFlow(SortOrder.DESCENDING)
    val sortOrder: StateFlow<SortOrder> get() = _sortOrder

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> get() = _filteredProducts

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> get() = _selectedProduct

    private val _priceUpdateComplete = MutableStateFlow<String?>(null)
    val priceUpdateComplete: StateFlow<String?> get() = _priceUpdateComplete

    private val _priceUpdateProgress = MutableStateFlow(0)
    val priceUpdateProgress: StateFlow<Int> get() = _priceUpdateProgress

    private val _stockUpdateProgress = MutableStateFlow(0)
    val stockUpdateProgress: StateFlow<Int> get() = _stockUpdateProgress

    private val _stockUpdateComplete = MutableStateFlow<String?>(null)
    val stockUpdateComplete: StateFlow<String?> get() = _stockUpdateComplete

    private val _invoiceCreationProgress = MutableStateFlow(0)
    val invoiceCreationProgress: StateFlow<Int> get() = _invoiceCreationProgress

    private val _invoiceCreationComplete = MutableStateFlow<String?>(null)
    val invoiceCreationComplete: StateFlow<String?> get() = _invoiceCreationComplete

    private val TAG = "ProductsViewModel"

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val query = _searchQuery.value
                val sortOrder = _sortOrder.value
                if (query.isBlank()) {
                    getAllProductUseCase.invoke(sortOrder).collectLatest { productList ->
                        _products.value = productList
                        _filteredProducts.value = productList
                        _isLoading.value = false
                    }
                } else {
                    getProductsUseCase.invoke(sortOrder,query).collectLatest { productList ->
                        _products.value = productList
                        _filteredProducts.value = productList
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Failed to load products: ${e.message}"
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            addProductUseCase(product)
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
                _selectedProduct.value = allProducts.find { it.id.value == productId }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProductStock(productId: Long, newStock: Int) {
        viewModelScope.launch {
            val product = _selectedProduct.value?.copy(stock = StockQuantity(newStock))
            product?.let {
                editProductUseCase(it)
                _selectedProduct.value = it
                loadProducts() // Refresh the list if it's visible
            }
        }
    }

    fun updateProductPrice(productId: Long, newPrice: Long) {
        viewModelScope.launch {
            val product = _selectedProduct.value?.copy(price = Money(newPrice))
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
