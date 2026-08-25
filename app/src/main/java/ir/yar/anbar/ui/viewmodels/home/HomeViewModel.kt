package ir.yar.anbar.ui.viewmodels.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.usecase.product.GetProductByBarcodeUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Single source of truth for barcode-scan UI state. One immutable snapshot keeps
 * product, loading, error and barcode from drifting apart across recompositions.
 */
data class BarcodeScanUiState(
    val scannedProduct: Product? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val detectedBarcode: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getProductByBarcodeUseCase: GetProductByBarcodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarcodeScanUiState())
    val uiState: StateFlow<BarcodeScanUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun searchProductByBarcode(barcode: String) {
        // Cancel any in-flight search so a slow older scan can't overwrite
        // the result of a newer one
        searchJob?.cancel()

        _uiState.update {
            it.copy(isLoading = true, errorMessage = null, scannedProduct = null)
        }

        searchJob = viewModelScope.launch {
            try {
                val product = getProductByBarcodeUseCase(barcode)
                _uiState.update { state ->
                    if (product == null) {
                        state.copy(
                            scannedProduct = null,
                            isLoading = false,
                            errorMessage = context.getString(
                                R.string.error_no_product_for_barcode, barcode
                            ),
                            detectedBarcode = barcode
                        )
                    } else {
                        state.copy(scannedProduct = product, isLoading = false)
                    }
                }
            } catch (e: CancellationException) {
                // Swallowing this would break structured concurrency
                throw e
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = context.getString(
                            R.string.error_product_search_failed, e.message ?: ""
                        ),
                        detectedBarcode = barcode
                    )
                }
            }
        }
    }

    fun clearScannedProduct() {
        _uiState.update { it.copy(scannedProduct = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearDetectedBarcode() {
        _uiState.update { it.copy(detectedBarcode = null) }
    }
}
