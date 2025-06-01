package com.example.composetrainer.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.domain.usecase.product.AddProductUseCase
import com.example.composetrainer.domain.usecase.product.DecreaseStockUseCase
import com.example.composetrainer.domain.usecase.product.DeleteProductUseCase
import com.example.composetrainer.domain.usecase.product.EditProductUseCase
import com.example.composetrainer.domain.usecase.product.GetProductUseCase
import com.example.composetrainer.domain.usecase.product.IncreaseStockUseCase
import com.example.composetrainer.domain.repository.InvoiceRepository
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
    private val decreaseStockUseCase: DecreaseStockUseCase,
    private val invoiceRepository: InvoiceRepository
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

    fun setRandomPricesForNullProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _priceUpdateProgress.value = 0
            try {
                // Get all products
                val allProducts = getProductsUseCase(SortOrder.DESCENDING, "").first()

                // Filter products with null prices
                val productsWithNullPrices = allProducts.filter { it.price == null }

                Log.d(TAG, "Found ${productsWithNullPrices.size} products with null prices")

                if (productsWithNullPrices.isEmpty()) {
                    _priceUpdateComplete.value = "No products with null prices found"
                    return@launch
                }

                // Update each product with a random price between 5000 and 945000
                productsWithNullPrices.forEachIndexed { index, product ->
                    val randomPrice = (5000..945000).random().toLong()
                    val updatedProduct = product.copy(price = randomPrice)
                    editProductUseCase(updatedProduct)
                    Log.d(TAG, "Updated product ${product.name} with price $randomPrice")

                    // Update progress
                    val progress = ((index + 1) * 100) / productsWithNullPrices.size
                    _priceUpdateProgress.value = progress

                    // Small delay to make progress visible for small datasets
                    if (productsWithNullPrices.size < 50) {
                        kotlinx.coroutines.delay(50)
                    }
                }

                // Refresh the products list
                loadProducts()

                Log.d(TAG, "Finished updating prices for ${productsWithNullPrices.size} products")

                // Notify completion
                _priceUpdateComplete.value =
                    "Updated prices for ${productsWithNullPrices.size} products"
            } catch (e: Exception) {
                Log.e(TAG, "Error updating null prices", e)
                _priceUpdateComplete.value = "Error updating prices: ${e.message}"
            } finally {
                _isLoading.value = false
                _priceUpdateProgress.value = 0
            }
        }
    }

    fun clearPriceUpdateMessage() {
        _priceUpdateComplete.value = null
    }

    fun setRandomStockForAllProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _stockUpdateProgress.value = 0
            try {
                // Get all products
                val allProducts = getProductsUseCase(SortOrder.DESCENDING, "").first()

                Log.d(TAG, "Found ${allProducts.size} products to update stock")

                if (allProducts.isEmpty()) {
                    _stockUpdateComplete.value = "No products found"
                    return@launch
                }

                // Update each product with a random stock between 1 and 500
                allProducts.forEachIndexed { index, product ->
                    val randomStock = (1..500).random()
                    val updatedProduct = product.copy(stock = randomStock)
                    editProductUseCase(updatedProduct)
                    Log.d(TAG, "Updated product ${product.name} with stock $randomStock")

                    // Update progress
                    val progress = ((index + 1) * 100) / allProducts.size
                    _stockUpdateProgress.value = progress

                    // Small delay to make progress visible for small datasets
                    if (allProducts.size < 50) {
                        kotlinx.coroutines.delay(30)
                    }
                }

                // Refresh the products list
                loadProducts()

                Log.d(TAG, "Finished updating stock for ${allProducts.size} products")

                // Notify completion
                _stockUpdateComplete.value = "Updated stock for ${allProducts.size} products"
            } catch (e: Exception) {
                Log.e(TAG, "Error updating stock", e)
                _stockUpdateComplete.value = "Error updating stock: ${e.message}"
            } finally {
                _isLoading.value = false
                _stockUpdateProgress.value = 0
            }
        }
    }

    fun createRandomInvoices() {
        viewModelScope.launch {
            _isLoading.value = true
            _invoiceCreationProgress.value = 0
            try {
                // Get all products first
                val allProducts = getProductsUseCase(SortOrder.DESCENDING, "").first()

                if (allProducts.isEmpty()) {
                    _invoiceCreationComplete.value = "No products available to create invoices"
                    return@launch
                }

                Log.d(
                    TAG,
                    "Creating 5 random invoices with products from ${allProducts.size} available products"
                )

                // Create 5 invoices
                repeat(5) { invoiceIndex ->
                    // Each invoice has between 4 to 14 products
                    val productCount = (4..14).random()
                    val invoiceProducts = mutableListOf<ProductWithQuantity>()

                    // Randomly select products for this invoice
                    val shuffledProducts = allProducts.shuffled()

                    repeat(productCount) { productIndex ->
                        if (productIndex < shuffledProducts.size) {
                            val product = shuffledProducts[productIndex]
                            // Each product has quantity between 1 to 8
                            val quantity = (1..8).random()
                            invoiceProducts.add(ProductWithQuantity(product, quantity))
                        }
                    }

                    // Create the invoice
                    if (invoiceProducts.isNotEmpty()) {
                        invoiceRepository.createInvoice(invoiceProducts)
                        Log.d(
                            TAG,
                            "Created invoice ${invoiceIndex + 1} with ${invoiceProducts.size} products"
                        )
                    }

                    // Update progress
                    val progress = ((invoiceIndex + 1) * 100) / 5
                    _invoiceCreationProgress.value = progress

                    // Small delay to make progress visible
                    kotlinx.coroutines.delay(200)
                }

                Log.d(TAG, "Finished creating 5 random invoices")
                _invoiceCreationComplete.value = "Created 5 random invoices successfully"

            } catch (e: Exception) {
                Log.e(TAG, "Error creating random invoices", e)
                _invoiceCreationComplete.value = "Error creating invoices: ${e.message}"
            } finally {
                _isLoading.value = false
                _invoiceCreationProgress.value = 0
            }
        }
    }

    fun clearStockUpdateMessage() {
        _stockUpdateComplete.value = null
    }

    fun clearInvoiceCreationMessage() {
        _invoiceCreationComplete.value = null
    }
}

enum class SortOrder {
    ASCENDING, // Oldest first
    DESCENDING // Newest first (default)
}

private const val TAG = "ProductsViewModel"
