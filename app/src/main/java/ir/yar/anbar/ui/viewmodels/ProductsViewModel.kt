package ir.yar.anbar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.usecase.product.AddProductUseCase
import ir.yar.anbar.domain.usecase.product.DecreaseStockUseCase
import ir.yar.anbar.domain.usecase.product.DeleteProductUseCase
import ir.yar.anbar.domain.usecase.product.EditProductUseCase
import ir.yar.anbar.domain.usecase.product.GetAllProductUseCase
import ir.yar.anbar.domain.usecase.product.GetProductByQueryUseCase
import ir.yar.anbar.domain.usecase.product.IncreaseStockUseCase
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
    private val decreaseStockUseCase: DecreaseStockUseCase,
    private val getProductByIdUseCase: GetProductByQueryUseCase
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
            addProductUseCase.invoke(product)
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
            try {
                editProductUseCase.invoke(product)
                loadProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    private fun cleanUpSelectedProduct() {
        _selectedProduct.value = null
    }
}

enum class SortOrder {
    ASCENDING, // Oldest first
    DESCENDING // Newest first (default)
}
