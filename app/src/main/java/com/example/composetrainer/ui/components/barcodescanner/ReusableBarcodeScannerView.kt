package com.example.composetrainer.ui.components.barcodescanner

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.utils.barcode.BarcodeSoundPlayer

// Data class for barcode scan result
data class BarcodeScanResult(
    val barcode: String,
    val productExists: Boolean
)

// Sealed class for different barcode scan actions
sealed class BarcodeScanAction {
    data class ProductFound(val barcode: String) : BarcodeScanAction()
    data class ProductNotFound(val barcode: String) : BarcodeScanAction()
}

// Configuration class for customizing behavior
data class BarcodeScanConfig(
    val playSuccessSound: Boolean = true,
    val showNotFoundToast: Boolean = true,
    val notFoundMessage: (Context, String) -> String = { context, barcode ->
        "${context.getString(R.string.no_product_found_with_barcode)}: $barcode"
    }
)

@Composable
fun ReusableBarcodeScannerView (
    products: List<Product>,
    onScanResult: (BarcodeScanAction) -> Unit,
    onClose: () -> Unit,
    config: BarcodeScanConfig = BarcodeScanConfig(),
    context: Context = LocalContext.current
){
    BarcodeScannerView(
        onBarcodeDetected = { barcode ->
            // Check if a product with this barcode exists
            val productExists = products.any { it.barcode?.value == barcode }

            if (productExists) {
                onScanResult(BarcodeScanAction.ProductFound(barcode))
                if (config.playSuccessSound) {
                    BarcodeSoundPlayer.playBarcodeSuccessSound(context)
                }
            } else {
                onScanResult(BarcodeScanAction.ProductNotFound(barcode))

                if (config.showNotFoundToast) {
                    Toast.makeText(
                        context,
                        config.notFoundMessage(context,barcode),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (config.playSuccessSound) {
                    BarcodeSoundPlayer.playBarcodeSuccessSound(context)
                }
            }
        },
        onClose = onClose
    )
}

