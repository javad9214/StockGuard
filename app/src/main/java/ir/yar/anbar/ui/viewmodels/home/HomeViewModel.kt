package ir.yar.anbar.ui.viewmodels.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.usecase.product.GetProductByBarcodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductByBarcodeUseCase: GetProductByBarcodeUseCase
): ViewModel() {

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    private val _scannedProduct = MutableStateFlow<Product?>(null)
    val scannedProduct: StateFlow<Product?> = _scannedProduct

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _detectedBarcode = MutableStateFlow<String?>(null)
    val detectedBarcode: StateFlow<String?> = _detectedBarcode

    fun searchProductByBarcode(barcode: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _scannedProduct.value = null

        viewModelScope.launch {
            try {
                val product = getProductByBarcodeUseCase(barcode)
                _scannedProduct.value = product
                _isLoading.value = false
                if (product == null) {
                    _errorMessage.value = "No product found with barcode: $barcode"
                    _detectedBarcode.value = barcode
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error searching for product: ${e.message}"
                _detectedBarcode.value = barcode
                _isLoading.value = false
            }
        }
    }

    fun clearScannedProduct() {
        _scannedProduct.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearDetectedBarcode() {
        _detectedBarcode.value = null
    }
}