package ir.yar.anbar.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.domain.model.InvoiceProductFactory
import ir.yar.anbar.domain.model.InvoiceType
import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductId
import ir.yar.anbar.domain.model.Quantity
import ir.yar.anbar.domain.model.addProduct
import ir.yar.anbar.domain.model.autoCreateInvoiceFromTemplate
import ir.yar.anbar.domain.model.removeProduct
import ir.yar.anbar.domain.model.updateProduct
import ir.yar.anbar.domain.model.updateQuantity
import ir.yar.anbar.domain.usecase.invoice.DeleteInvoiceUseCase
import ir.yar.anbar.domain.usecase.invoice.GetInvoiceNumberUseCase
import ir.yar.anbar.domain.usecase.invoice.InitInvoiceWithProductsUseCase
import ir.yar.anbar.domain.usecase.invoice.InsertInvoiceUseCase
import ir.yar.anbar.domain.usecase.product.CheckProductStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val initInvoiceWithProductsUseCase: InitInvoiceWithProductsUseCase,
    private val deleteInvoiceUseCase: DeleteInvoiceUseCase,
    private val insertInvoiceUseCase: InsertInvoiceUseCase,
    private val getInvoiceNumberUseCase: GetInvoiceNumberUseCase,
    private val checkProductStockUseCase: CheckProductStockUseCase
) : ViewModel() {

    // UI State - StateFlow
    private val _uiState = MutableStateFlow(InvoiceUiState())
    val uiState = _uiState.asStateFlow()

    // One-time events - Channel
    private val _events = Channel<InvoiceEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _saveInvoiceLoading = MutableStateFlow(false)
    val saveInvoiceLoading = _saveInvoiceLoading.asStateFlow()

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
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to init invoice: ${e.message}"
                    )
                }
            }
        }
    }

    fun addToCurrentInvoice(product: Product, quantity: Int) {
        _uiState.update { state ->
            val currentInvoice = state.currentInvoice
            val existingItem = currentInvoice.invoiceProducts.find {
                it.productId == product.id
            }

            val availableStock = product.stock.value
            val isSale = currentInvoice.invoice.invoiceType == InvoiceType.SALE

            val safeQuantityToAdd = if (isSale) {
                quantity.coerceAtMost(availableStock)
            } else {
                quantity
            }

            val updatedInvoice = if (existingItem != null) {
                val newTotalQuantity = existingItem.quantity.value + safeQuantityToAdd
                val finalQuantity = if (isSale) {
                    newTotalQuantity.coerceAtMost(availableStock)
                } else {
                    newTotalQuantity
                }

                currentInvoice.updateProduct(
                    productId = product.id,
                    updater = { invoiceProduct ->
                        invoiceProduct.copy(quantity = Quantity(finalQuantity))
                    }
                )
            } else {
                val newItem = InvoiceProductFactory.create(
                    invoiceId = currentInvoice.invoiceId,
                    productId = product.id,
                    quantity = Quantity(safeQuantityToAdd),
                    priceAtSale = product.price,
                    costPriceAtTransaction = product.costPrice
                )
                currentInvoice.addProduct(newItem, product)
            }

            state.copy(currentInvoice = updatedInvoice)
        }
    }

    fun removeFromCurrentInvoice(productId: ProductId) {
        _uiState.update { state ->
            state.copy(
                currentInvoice = state.currentInvoice.removeProduct(productId)
            )
        }
    }

    fun updateItemQuantity(productId: Long, newQuantity: Int) {
        _uiState.update { state ->
            val currentInvoice = state.currentInvoice
            val isSale = currentInvoice.invoice.invoiceType == InvoiceType.SALE

            val updatedInvoiceProducts = currentInvoice.invoiceProducts.map { invoiceProduct ->
                if (invoiceProduct.productId.value == productId) {
                    val availableStock = currentInvoice.products
                        .find { it.id == ProductId(productId) }
                        ?.stock?.value ?: 0

                    val safeQuantity = if (isSale) {
                        newQuantity.coerceAtMost(availableStock)
                    } else {
                        newQuantity
                    }

                    invoiceProduct.updateQuantity(safeQuantity)
                } else {
                    invoiceProduct
                }
            }

            state.copy(
                currentInvoice = currentInvoice.copy(
                    invoiceProducts = updatedInvoiceProducts
                )
            )
        }
    }

    fun saveInvoice() {
        viewModelScope.launch {
            // Set loading state
            _saveInvoiceLoading.value = true

            try {
                // Get current invoice and prepare it for saving
                val currentInvoice = _uiState.value.currentInvoice
                val finalInvoice = currentInvoice.autoCreateInvoiceFromTemplate()
                val invoiceToSave = currentInvoice.copy(invoice = finalInvoice)

                // Save invoice - this operation must complete even if user navigates away
                withContext(NonCancellable) {
                    insertInvoiceUseCase.invoke(invoiceToSave)
                }

                // Update state: clear invoice, stop loading, clear errors
                _uiState.update {
                    it.copy(
                        currentInvoice = InvoiceWithProducts.empty(),
                        isLoading = false,
                        errorMessage = null
                    )
                }

                _saveInvoiceLoading.value = false
                // Send success event for navigation
                _events.send(InvoiceEvent.SaveSuccess)

            } catch (e: Exception) {
                // Handle error: stop loading, show error message
                val errorMsg = "Failed to create invoice: ${e.message}"

                _saveInvoiceLoading.value = false
                // Send error event (for toast/snackbar)
                _events.send(InvoiceEvent.SaveError(errorMsg))

                // Log for debugging
                Log.e("InvoiceViewModel", "Error saving invoice", e)
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
        _uiState.update { it.copy(currentInvoice = InvoiceWithProducts.empty())  }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    data class InvoiceUiState(
        val currentInvoice: InvoiceWithProducts = InvoiceWithProducts.empty(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface InvoiceEvent {
        object SaveSuccess : InvoiceEvent
        data class SaveError(val message: String?) : InvoiceEvent
    }

    companion object {
        const val TAG = "InvoiceViewModel"
    }
}