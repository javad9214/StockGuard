package ir.yar.anbar.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.InvoiceType
import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductId
import ir.yar.anbar.domain.model.addProductToInvoice
import ir.yar.anbar.domain.model.autoCreateInvoiceFromTemplate
import ir.yar.anbar.domain.model.removeProduct
import ir.yar.anbar.domain.model.updateProductQuantity
import ir.yar.anbar.domain.usecase.invoice.InitInvoiceWithProductsUseCase
import ir.yar.anbar.domain.usecase.invoice.InsertInvoiceUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val initInvoiceWithProductsUseCase: InitInvoiceWithProductsUseCase,
    private val insertInvoiceUseCase: InsertInvoiceUseCase
) : ViewModel() {

    // UI State - StateFlow
    private val _uiState = MutableStateFlow(InvoiceUiState())
    val uiState = _uiState.asStateFlow()

    // One-time events - Channel
    private val _events = Channel<InvoiceEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        initCurrentInvoice()
    }

    private fun initCurrentInvoice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val initialInvoice = initInvoiceWithProductsUseCase.invoke()
                _uiState.update {
                    it.copy(
                        currentInvoice = initialInvoice,
                        isLoading = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = context.getString(R.string.error_failed_to_init_invoice, e.message ?: "")
                    )
                }
            }
        }
    }

    fun addToCurrentInvoice(product: Product, quantity: Int) {
        _uiState.update { state ->
            state.copy(
                currentInvoice = state.currentInvoice.addProductToInvoice(product, quantity)
            )
        }
    }

    fun removeFromCurrentInvoice(productId: ProductId) {
        _uiState.update { state ->
            state.copy(
                currentInvoice = state.currentInvoice.removeProduct(productId)
            )
        }
    }

    fun updateItemQuantity(productId: ProductId, newQuantity: Int) {
        _uiState.update { state ->
            state.copy(
                currentInvoice = state.currentInvoice.updateProductQuantity(productId, newQuantity)
            )
        }
    }

    fun saveInvoice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                // Get current invoice and prepare it for saving
                val currentInvoice = _uiState.value.currentInvoice
                val finalInvoice = currentInvoice.autoCreateInvoiceFromTemplate()
                val invoiceToSave = currentInvoice.copy(invoice = finalInvoice)

                // Save invoice - this operation must complete even if user navigates away
                withContext(NonCancellable) {
                    insertInvoiceUseCase.invoke(invoiceToSave)
                }

                // Update state: reset invoice, stop loading, clear errors
                _uiState.update {
                    it.copy(
                        currentInvoice = InvoiceWithProducts.empty(),
                        isLoading = false,
                        isSaving = false,
                        errorMessage = null
                    )
                }

                // Send success event for navigation
                _events.send(InvoiceEvent.SaveSuccess)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = context.getString(
                            R.string.error_failed_to_create_invoice, e.message ?: ""
                        )
                    )
                }
                Log.e(TAG, "Error saving invoice", e)
            }
        }
    }

    fun changeInvoiceType(invoiceType: InvoiceType) {
        _uiState.update { state ->
            state.copy(
                currentInvoice = state.currentInvoice.copy(
                    invoice = state.currentInvoice.invoice.copy(
                        invoiceType = invoiceType
                    )
                )
            )
        }
    }

    fun clearCurrentInvoice() {
        _uiState.update { it.copy(currentInvoice = InvoiceWithProducts.empty()) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    data class InvoiceUiState(
        val currentInvoice: InvoiceWithProducts = InvoiceWithProducts.empty(),
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface InvoiceEvent {
        data object SaveSuccess : InvoiceEvent
    }

    companion object {
        private const val TAG = "InvoiceViewModel"
    }
}
