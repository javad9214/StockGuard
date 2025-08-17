package com.example.composetrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.repository.ProductRepository
import com.example.composetrainer.domain.usecase.invoice.DeleteInvoiceUseCase
import com.example.composetrainer.domain.usecase.invoice.GetAllInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceListViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val deleteInvoiceUseCase: DeleteInvoiceUseCase,
    private val getAllInvoiceUseCase: GetAllInvoiceUseCase
) : ViewModel() {


    private val _selectedProducts = MutableStateFlow<List<Product>>(emptyList())
    val selectedProducts: StateFlow<List<Product>> get() = _selectedProducts

    private val _invoices = MutableStateFlow<List<InvoiceWithProducts>>(emptyList())
    val invoices: StateFlow<List<InvoiceWithProducts>> get() = _invoices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage


    private val _sortNewestFirst = MutableStateFlow(true)
    val sortNewestFirst: StateFlow<Boolean> get() = _sortNewestFirst

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> get() = _isSelectionMode

    private val _selectedInvoices = MutableStateFlow<Set<Long>>(emptySet())
    val selectedInvoices: StateFlow<Set<Long>> get() = _selectedInvoices

    private val _showDeleteConfirmationDialog = MutableStateFlow(false)
    val showDeleteConfirmationDialog: StateFlow<Boolean> get() = _showDeleteConfirmationDialog

    init {
        loadInvoices()
    }

    fun toggleSortOrder() {
        _sortNewestFirst.value = !_sortNewestFirst.value
        loadInvoices()
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (_sortNewestFirst.value) {
                    getAllInvoiceUseCase.invoke().collectLatest { invoices ->
                        _invoices.value = invoices
                        _isLoading.value = false
                    }
                } else {
                    invoiceRepository.getAllInvoicesOldestFirst().collectLatest { invoices ->
                        _invoices.value = invoices
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }


    fun deleteInvoice(invoiceId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                deleteInvoiceUseCase(invoiceId)
                loadInvoices() // Refresh invoice list
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete invoice: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSelectedInvoices() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                deleteInvoiceUseCase.deleteMultiple(_selectedInvoices.value.toList())
                _selectedInvoices.value = emptySet()
                _isSelectionMode.value = false
                _showDeleteConfirmationDialog.value = false
                loadInvoices() // Refresh invoice list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete selected invoices: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Selection Mode Functions
    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
        if (!_isSelectionMode.value) {
            _selectedInvoices.value = emptySet()
        }
    }

    fun toggleInvoiceSelection(invoiceId: Long) {
        val currentSelection = _selectedInvoices.value.toMutableSet()
        if (currentSelection.contains(invoiceId)) {
            currentSelection.remove(invoiceId)
        } else {
            currentSelection.add(invoiceId)
        }
        _selectedInvoices.value = currentSelection
    }

    fun isInvoiceSelected(invoiceId: Long): Boolean {
        return _selectedInvoices.value.contains(invoiceId)
    }

    fun clearSelection() {
        _selectedInvoices.value = emptySet()
    }

    fun showDeleteConfirmationDialog() {
        _showDeleteConfirmationDialog.value = true
    }

    fun dismissDeleteConfirmationDialog() {
        _showDeleteConfirmationDialog.value = false
    }
}