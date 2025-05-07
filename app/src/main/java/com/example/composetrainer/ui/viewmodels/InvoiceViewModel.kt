package com.example.composetrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val productRepository: ProductRepository
): ViewModel() {

    private val _currentInvoice = MutableStateFlow<List<ProductWithQuantity>>(emptyList())
    val currentInvoice: StateFlow<List<ProductWithQuantity>> get() = _currentInvoice

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> get() = _invoices

    private val _isLoading = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _nextInvoiceNumber = MutableStateFlow<Long?>(null)
    val nextInvoiceNumber: StateFlow<Long?> get() = _nextInvoiceNumber

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products


    init {
        loadInvoices()
        loadProducts()
    }

    fun loadInvoices(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                invoiceRepository.getAllInvoices().collectLatest { invoices ->
                    _invoices.value = invoices.map { it.toDomain() }
                    _isLoading.value = false
                }
            }catch (e: Exception){
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                productRepository.getAllProducts().collectLatest { productList ->
                    _products.value = productList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load products: ${e.message}"
            }
        }
    }

    fun addToCurrentInvoice(product: Product, quantity: Int){
        val existingItem = _currentInvoice.value.find { it.product.id == product.id }
        val updatedList = if (existingItem != null) {
            _currentInvoice.value.map {
                if (it.product.id == product.id) {
                    it.copy(quantity = it.quantity + quantity)
                } else it
            }
        }else {
            _currentInvoice.value + ProductWithQuantity(product, quantity)
        }
        _currentInvoice.value = updatedList
    }

    fun removeFromCurrentInvoice(productId: Long){
        _currentInvoice.value = _currentInvoice.value.filter { it.product.id != productId }
    }

    fun createInvoice(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                invoiceRepository.createInvoice(_currentInvoice.value)
                _currentInvoice.value = emptyList()
                loadInvoices() // Refresh invoice list
                _errorMessage.value = null
            }catch(e: Exception) {
                _errorMessage.value = "Failed to create invoice: ${e.message}"
            }finally {
                _isLoading.value = false
            }

        }
    }


    fun deleteInvoice(invoiceId: Long){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                invoiceRepository.deleteInvoice(invoiceId)
                loadInvoices() // Refresh invoice list
                _errorMessage.value = null
            }catch (e: Exception){
                _errorMessage.value = "Failed to delete invoice: ${e.message}"
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCurrentInvoice(){
        _currentInvoice.value = emptyList()
    }

    fun getNextInvoiceNumberId() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val nextNumber = invoiceRepository.getNextInvoiceNumberId()
                _nextInvoiceNumber.value = nextNumber
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to get next invoice number: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun calculateTotalPrice(): Long {
        return _currentInvoice.value.sumOf {
            it.product.price?.times(it.quantity) ?: 0L
        }
    }
}