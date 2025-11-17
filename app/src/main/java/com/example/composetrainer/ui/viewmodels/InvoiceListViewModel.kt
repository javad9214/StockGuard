package com.example.composetrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.InvoiceType
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.usecase.invoice.DeleteInvoiceUseCase
import com.example.composetrainer.domain.usecase.invoice.GetAllInvoiceUseCase
import com.example.composetrainer.domain.repository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Invoice List Screen
 */
data class InvoiceListUiState(
    val invoices: List<InvoiceWithProducts> = emptyList(),
    val filteredInvoices: List<InvoiceWithProducts> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sortNewestFirst: Boolean = true,
    val selectedTypeFilter: InvoiceType? = null,
    val isSelectionMode: Boolean = false,
    val selectedInvoices: Set<Long> = emptySet(),
    val showDeleteConfirmationDialog: Boolean = false
)

/**
 * UI Events for Invoice List Screen
 */
sealed class InvoiceListEvent {
    data object ToggleSortOrder : InvoiceListEvent()
    data class FilterByType(val type: InvoiceType?) : InvoiceListEvent()
    data object ToggleSelectionMode : InvoiceListEvent()
    data class ToggleInvoiceSelection(val invoiceId: Long) : InvoiceListEvent()
    data object ClearSelection : InvoiceListEvent()
    data object ShowDeleteConfirmation : InvoiceListEvent()
    data object DismissDeleteConfirmation : InvoiceListEvent()
    data class DeleteInvoice(val invoiceId: Long) : InvoiceListEvent()
    data object DeleteSelectedInvoices : InvoiceListEvent()
    data object ClearError : InvoiceListEvent()
}

@HiltViewModel
class InvoiceListViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val deleteInvoiceUseCase: DeleteInvoiceUseCase,
    private val getAllInvoiceUseCase: GetAllInvoiceUseCase
) : ViewModel() {

    // Single source of truth for UI state
    private val _uiState = MutableStateFlow(InvoiceListUiState())
    val uiState: StateFlow<InvoiceListUiState> = _uiState.asStateFlow()

    init {
        loadInvoices()
    }

    /**
     * Handle UI events
     */
    fun onEvent(event: InvoiceListEvent) {
        when (event) {
            is InvoiceListEvent.ToggleSortOrder -> toggleSortOrder()
            is InvoiceListEvent.FilterByType -> filterByType(event.type)
            is InvoiceListEvent.ToggleSelectionMode -> toggleSelectionMode()
            is InvoiceListEvent.ToggleInvoiceSelection -> toggleInvoiceSelection(event.invoiceId)
            is InvoiceListEvent.ClearSelection -> clearSelection()
            is InvoiceListEvent.ShowDeleteConfirmation -> showDeleteConfirmation()
            is InvoiceListEvent.DismissDeleteConfirmation -> dismissDeleteConfirmation()
            is InvoiceListEvent.DeleteInvoice -> deleteInvoice(event.invoiceId)
            is InvoiceListEvent.DeleteSelectedInvoices -> deleteSelectedInvoices()
            is InvoiceListEvent.ClearError -> clearError()
        }
    }

    /**
     * Load invoices from repository
     * Uses background dispatcher to avoid blocking main thread
     */
    private fun loadInvoices() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val invoicesFlow = if (_uiState.value.sortNewestFirst) {
                    getAllInvoiceUseCase.invoke()
                } else {
                    invoiceRepository.getAllInvoicesOldestFirst()
                }

                invoicesFlow
                    .flowOn(Dispatchers.IO) // Process on IO dispatcher
                    .catch { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Unknown error occurred"
                            )
                        }
                    }
                    .collect { invoices ->
                        _uiState.update { currentState ->
                            val filtered = applyFilter(invoices, currentState.selectedTypeFilter)
                            currentState.copy(
                                invoices = invoices,
                                filteredInvoices = filtered,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load invoices"
                    )
                }
            }
        }
    }

    /**
     * Apply type filter to invoices
     * Runs on Default dispatcher for CPU-intensive filtering
     */
    private fun applyFilter(
        invoices: List<InvoiceWithProducts>,
        typeFilter: InvoiceType?
    ): List<InvoiceWithProducts> {
        return if (typeFilter == null) {
            invoices
        } else {
            invoices.filter { it.invoice.invoiceType == typeFilter }
        }
    }

    /**
     * Toggle sort order between newest and oldest first
     */
    private fun toggleSortOrder() {
        _uiState.update { it.copy(sortNewestFirst = !it.sortNewestFirst) }
        loadInvoices()
    }

    /**
     * Filter invoices by type
     * Reapplies filter without reloading from repository
     */
    private fun filterByType(type: InvoiceType?) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { currentState ->
                val filtered = applyFilter(currentState.invoices, type)
                currentState.copy(
                    selectedTypeFilter = type,
                    filteredInvoices = filtered
                )
            }
        }
    }

    /**
     * Delete a single invoice
     */
    private fun deleteInvoice(invoiceId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { it.copy(isLoading = true) }
                deleteInvoiceUseCase(invoiceId)
                _uiState.update { it.copy(errorMessage = null) }
                // No need to call loadInvoices() - the Flow will auto-update
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to delete invoice: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Delete multiple selected invoices
     */
    private fun deleteSelectedInvoices() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val selectedIds = _uiState.value.selectedInvoices.toList()
                deleteInvoiceUseCase.deleteMultiple(selectedIds)

                _uiState.update {
                    it.copy(
                        selectedInvoices = emptySet(),
                        isSelectionMode = false,
                        showDeleteConfirmationDialog = false,
                        errorMessage = null
                    )
                }
                // No need to call loadInvoices() - the Flow will auto-update
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to delete selected invoices: ${e.message}"
                    )
                }
            }
        }
    }

    // Selection Mode Functions
    private fun toggleSelectionMode() {
        _uiState.update {
            it.copy(
                isSelectionMode = !it.isSelectionMode,
                selectedInvoices = if (!it.isSelectionMode) it.selectedInvoices else emptySet()
            )
        }
    }

    private fun toggleInvoiceSelection(invoiceId: Long) {
        _uiState.update { currentState ->
            val currentSelection = currentState.selectedInvoices.toMutableSet()
            if (currentSelection.contains(invoiceId)) {
                currentSelection.remove(invoiceId)
            } else {
                currentSelection.add(invoiceId)
            }
            currentState.copy(selectedInvoices = currentSelection)
        }
    }

    private fun clearSelection() {
        _uiState.update { it.copy(selectedInvoices = emptySet()) }
    }

    private fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmationDialog = true) }
    }

    private fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmationDialog = false) }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Helper function to check if invoice is selected
     */
    fun isInvoiceSelected(invoiceId: Long): Boolean {
        return _uiState.value.selectedInvoices.contains(invoiceId)
    }
}