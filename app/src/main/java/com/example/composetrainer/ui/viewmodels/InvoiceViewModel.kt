package com.example.composetrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.InvoiceNumber
import com.example.composetrainer.domain.model.InvoiceProductFactory
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductId
import com.example.composetrainer.domain.model.Quantity
import com.example.composetrainer.domain.usecase.invoice.CheckProductStockUseCase
import com.example.composetrainer.domain.usecase.invoice.DeleteInvoiceUseCase
import com.example.composetrainer.domain.usecase.invoice.GetInvoiceNumberUseCase
import com.example.composetrainer.domain.usecase.invoice.InsertInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val deleteInvoiceUseCase: DeleteInvoiceUseCase,
    private val insertInvoiceUseCase: InsertInvoiceUseCase,
    private val getInvoiceNumberUseCase: GetInvoiceNumberUseCase,
    private val checkProductStockUseCase: CheckProductStockUseCase
) : ViewModel() {

    private val _currentInvoice = MutableStateFlow(InvoiceWithProducts.empty())
    val currentInvoice: StateFlow<InvoiceWithProducts> get() = _currentInvoice

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage
    
    init {
        initCurrentInvoice()
    }
    
    private fun initCurrentInvoice(){

        val invoiceWithProductsDraft = InvoiceWithProducts.createDefault()
        _currentInvoice.value = invoiceWithProductsDraft
    }


    fun addToCurrentInvoice(product: Product, quantity: Int) {
        // Find the existing item in the current invoice
        val existingItem = _currentInvoice.value.invoiceProducts.find{
             item -> item.productId == product.id 
        }

        val availableStock = product.stock
        val safeQuantityToAdd = if (quantity > availableStock.value) availableStock.value else quantity

        val updatedList = if (existingItem != null) {
            // Calculate the new total quantity for the existing item
            val newTotalQuantity = existingItem.quantity.value + safeQuantityToAdd
            val finalQuantity = if (newTotalQuantity > availableStock.value) availableStock.value else newTotalQuantity

            // Update the existing invoice item
            _currentInvoice.value.copy(
                invoiceProducts = _currentInvoice.value.invoiceProducts.map { productItem ->
                    if (productItem.productId == product.id) {
                        productItem.copy(quantity = Quantity(finalQuantity))
                    } else {
                        productItem
                    }
                }
            )
        } else {
            // Create a new invoiceProduct item if no existing item is found
            val newItem = InvoiceProductFactory.create(
                invoiceId = _currentInvoice.value.invoiceId,
                productId = product.id,
                quantity = Quantity(safeQuantityToAdd),
                priceAtSale = product.price,
                costPriceAtTransaction = product.costPrice
            )
            _currentInvoice.value.copy(
                invoiceProducts = _currentInvoice.value.invoiceProducts + newItem
            )
        }

        // Update the current invoice state
        _currentInvoice.value = updatedList
    }
    
    fun removeFromCurrentInvoice(productId: Long) {
        _currentInvoice.value = _currentInvoice.value.copy(
            invoiceProducts = _currentInvoice.value.invoiceProducts.filter { it.productId.value != productId }
        )
    }

    fun updateItemQuantity(productId: Long, newQuantity: Int) {
        _currentInvoice.value = _currentInvoice.value.copy(
            invoiceProducts = _currentInvoice.value.invoiceProducts.map { invoiceProduct ->
                if (invoiceProduct.productId.value == productId) {
                    // Get available stock from the product
                    val availableStock = _currentInvoice.value.products.find { it.id == ProductId(productId) }?.stock?.value ?: 0
                    // Ensure new quantity doesn't exceed stock
                    val safeQuantity = if (newQuantity > availableStock) availableStock else newQuantity
                    invoiceProduct.copy(quantity = Quantity(safeQuantity))
                } else {
                    invoiceProduct
                }
            }
        )
    }

    fun saveInvoice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val invoiceNumber = getInvoiceNumberUseCase.invoke()
                val invoiceWithProductsFinal = InvoiceWithProducts.createWithCalculatedTotals(
                    invoiceNumber = InvoiceNumber(invoiceNumber),
                    domainProducts = _currentInvoice.value.products,
                    invoiceProducts = _currentInvoice.value.invoiceProducts
                )

                insertInvoiceUseCase.invoke(invoiceWithProductsFinal)
                _currentInvoice.value = InvoiceWithProducts.empty()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create invoice: ${e.message}"
            } finally {
                _isLoading.value = false
            }

        }
    }

    fun clearCurrentInvoice() {
        _currentInvoice.value = InvoiceWithProducts.empty()
    }
}