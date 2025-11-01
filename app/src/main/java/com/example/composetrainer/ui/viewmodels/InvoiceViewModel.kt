package com.example.composetrainer.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.InvoiceProductFactory
import com.example.composetrainer.domain.model.InvoiceType
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductId
import com.example.composetrainer.domain.model.Quantity
import com.example.composetrainer.domain.model.addProduct
import com.example.composetrainer.domain.model.autoCreateInvoiceFromTemplate
import com.example.composetrainer.domain.model.removeProduct
import com.example.composetrainer.domain.model.updateProduct
import com.example.composetrainer.domain.model.updateQuantity
import com.example.composetrainer.domain.usecase.invoice.DeleteInvoiceUseCase
import com.example.composetrainer.domain.usecase.invoice.GetInvoiceNumberUseCase
import com.example.composetrainer.domain.usecase.invoice.InitInvoiceWithProductsUseCase
import com.example.composetrainer.domain.usecase.invoice.InsertInvoiceUseCase
import com.example.composetrainer.domain.usecase.product.CheckProductStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val initInvoiceWithProductsUseCase: InitInvoiceWithProductsUseCase,
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

    private fun initCurrentInvoice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.i(TAG, "initCurrentInvoice: InvoiceViewModel")
                val initialInvoice = initInvoiceWithProductsUseCase.invoke()
                _currentInvoice.value = initialInvoice
            } catch (e: Exception) {
                _errorMessage.value = "Failed to init invoice: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
        val invoiceWithProductsDraft = InvoiceWithProducts.createDefault()
        _currentInvoice.value = invoiceWithProductsDraft
    }

    fun addToCurrentInvoice(product: Product, quantity: Int) {

        // Find the existing item in the current invoice
        val existingItem = _currentInvoice.value.invoiceProducts.find { item ->
            item.productId == product.id
        }

        val availableStock = product.stock


        val safeQuantityToAdd = if (_currentInvoice.value.invoice.invoiceType == InvoiceType.SALE) {
            if (quantity > availableStock.value) availableStock.value else quantity
        } else {
            quantity // For PURCHASE, allow any quantity
        }

        val updatedList = if (existingItem != null) {
            // Calculate the new total quantity for the existing item
            val newTotalQuantity = existingItem.quantity.value + safeQuantityToAdd


            val finalQuantity = if (_currentInvoice.value.invoice.invoiceType == InvoiceType.SALE) {
                if (newTotalQuantity > availableStock.value) availableStock.value else newTotalQuantity
            } else {
                newTotalQuantity // For PURCHASE, allow any quantity
            }

            // Update the existing invoice item
            _currentInvoice.value.updateProduct(
                productId = product.id,
                updater = { invoiceProduct ->
                    invoiceProduct.copy(quantity = Quantity(finalQuantity))
                })
        } else {
            // Create a new invoiceProduct item if no existing item is found
            val newItem = InvoiceProductFactory.create(
                invoiceId = _currentInvoice.value.invoiceId,
                productId = product.id,
                quantity = Quantity(safeQuantityToAdd),
                priceAtSale = product.price,
                costPriceAtTransaction = product.costPrice
            )
            _currentInvoice.value.addProduct(newItem, product)
        }

        // Update the current invoice state
        _currentInvoice.value = updatedList
    }

    fun removeFromCurrentInvoice(productId: ProductId) {
        _currentInvoice.value = _currentInvoice.value.removeProduct(productId)
    }

    fun updateItemQuantity(productId: Long, newQuantity: Int) {
        _currentInvoice.value = _currentInvoice.value.copy(
            invoiceProducts = _currentInvoice.value.invoiceProducts.map { invoiceProduct ->
                if (invoiceProduct.productId.value == productId) {
                    // Get available stock from the product
                    val availableStock =
                        _currentInvoice.value.products.find { it.id == ProductId(productId) }?.stock?.value
                            ?: 0

                    val safeQuantity = if (_currentInvoice.value.invoice.invoiceType == InvoiceType.SALE) {
                        if (newQuantity > availableStock) availableStock else newQuantity
                    } else {
                        newQuantity // For PURCHASE, allow any quantity
                    }

                    invoiceProduct.updateQuantity(safeQuantity)
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
                val finalInvoice = _currentInvoice.value.autoCreateInvoiceFromTemplate()
                _currentInvoice.value = _currentInvoice.value.copy(invoice = finalInvoice)
                insertInvoiceUseCase.invoke(_currentInvoice.value)
                _currentInvoice.value = InvoiceWithProducts.empty()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create invoice: ${e.message}"
            } finally {
                _isLoading.value = false
            }

        }
    }

    fun changeInvoiceType(invoiceType: InvoiceType) {
        _currentInvoice.value = _currentInvoice.value.copy(
            invoice = _currentInvoice.value.invoice.copy(
                invoiceType = invoiceType
            )
        )
    }

    fun clearCurrentInvoice() {
        _currentInvoice.value = InvoiceWithProducts.empty()
    }

    companion object {
        const val TAG = "InvoiceViewModel"
    }
}