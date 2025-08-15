package com.example.composetrainer.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.Barcode
import com.example.composetrainer.domain.model.InvoiceProduct
import com.example.composetrainer.domain.model.InvoiceProductFactory
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.ProductFactory
import com.example.composetrainer.domain.model.ProductName
import com.example.composetrainer.domain.model.StockQuantity
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.domain.usecase.invoice.InitInvoiceWithProductsUseCase
import com.example.composetrainer.domain.usecase.invoice.InsertInvoiceUseCase
import com.example.composetrainer.domain.usecase.product.AddProductUseCase
import com.example.composetrainer.domain.usecase.product.GetProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val insertInvoiceUseCase: InsertInvoiceUseCase,
    private val getProductsUseCase: GetProductUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val initInvoiceWithProductsUseCase: InitInvoiceWithProductsUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

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

    private val TAG = "SettingViewModel"

    fun addRandomProducts() {
        viewModelScope.launch {
            repeat(5) {
                val randomProduct = ProductFactory.createBasic(
                    name = ProductName("Product " + ('A'..'Z').random() + (100..999).random()),
                    barcode = Barcode((100000000000..999999999999).random().toString()),
                    price = listOf(1399L, 1450L, 1700L, 1850L, 2400L).random(),
                    costPrice = listOf(100L, 250L, 499L, 999L, 1299L).random(),

                    )
                addProductUseCase(randomProduct)
            }
        }
    }

    fun createRandomInvoices() {
        viewModelScope.launch {
            _isLoading.value = true
            _invoiceCreationProgress.value = 0
            try {
                val invoiceWithProducts: InvoiceWithProducts =
                    initInvoiceWithProductsUseCase.invoke()
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
                    val invoiceProducts = mutableListOf<InvoiceProduct>()

                    // Randomly select products for this invoice
                    val shuffledProducts = allProducts.shuffled()

                    repeat(productCount) { productIndex ->
                        if (productIndex < shuffledProducts.size) {
                            val product = shuffledProducts[productIndex]
                            // Each product has quantity between 1 to 8
                            val quantity = (1..8).random()
                            invoiceProducts.add(
                                InvoiceProduct(
                                    invoiceId = invoiceWithProducts.invoiceId,
                                    productId = product.productId,
                                )
                            )
                        }
                    }

                    // Create the invoice
                    if (invoiceProducts.isNotEmpty()) {
                        insertInvoiceUseCase.invoke(invoiceProducts)
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

    fun setRandomCostPrice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get all products
                val allProducts = getProductsUseCase(SortOrder.DESCENDING, "").first()

                // Update each product with a random cost price between 1000 and 500000
                allProducts.forEach { product ->
                    val randomCostPrice = (product.price ?: 0L) - (10..1000).random()
                    val updatedProduct = product.copy(costPrice = randomCostPrice)
                    editProductUseCase(updatedProduct)
                    Log.d(TAG, "Updated product ${product.name} with cost price $randomCostPrice")
                }

                // Refresh the products list
                loadProducts()

                Log.d(TAG, "Finished updating cost prices for ${allProducts.size} products")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating cost prices", e)
            } finally {
                _isLoading.value = false
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
                val productsWithNullPrices = allProducts.filter { it.price.isZero() }

                Log.d(TAG, "Found ${productsWithNullPrices.size} products with null prices")

                if (productsWithNullPrices.isEmpty()) {
                    _priceUpdateComplete.value = "No products with null prices found"
                    return@launch
                }

                // Update each product with a random price between 5000 and 945000
                productsWithNullPrices.forEachIndexed { index, product ->
                    val randomPrice = (5000..945000).random().toLong()
                    val updatedProduct = product.copy(price = Money(randomPrice))
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
                    val updatedProduct = product.copy(stock = StockQuantity(randomStock))
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

    fun clearStockUpdateMessage() {
        _stockUpdateComplete.value = null
    }

    fun clearInvoiceCreationMessage() {
        _invoiceCreationComplete.value = null
    }
}