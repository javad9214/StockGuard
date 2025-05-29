package com.example.composetrainer.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.usecase.GetProductByBarcodeUseCase
import com.example.composetrainer.ui.screens.invoice.productselection.ProductSelectionBottomSheet
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.theme.Kamran
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.ui.viewmodels.HomeViewModel
import com.example.composetrainer.ui.components.BarcodeScannerView
import com.example.composetrainer.ui.navigation.Screen
import com.example.composetrainer.utils.ProductImporter
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.str
import kotlinx.coroutines.flow.flow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onButtonClick: () -> Unit,
    onToggleTheme: () -> Unit = {},
    isDarkTheme: Boolean = false,
    navController: NavController = rememberNavController(),
    viewModel: ProductsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel,
    invoiceViewModel: InvoiceViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val progress by homeViewModel.progress.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBarcodeScannerView by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Observe scanned product
    val scannedProduct by homeViewModel.scannedProduct.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()

    // Observe price update completion
    val priceUpdateMessage by viewModel.priceUpdateComplete.collectAsState()
    val priceUpdateProgress by viewModel.priceUpdateProgress.collectAsState()
    val productsLoading by viewModel.isLoading.collectAsState()

    // Observe stock update completion
    val stockUpdateMessage by viewModel.stockUpdateComplete.collectAsState()
    val stockUpdateProgress by viewModel.stockUpdateProgress.collectAsState()

    // Handle navigation when product is found
    LaunchedEffect(scannedProduct) {
        scannedProduct?.let { product ->
            // Add debug log to verify product is found and being added
            Log.d(TAG, "Product found: ${product.name}, ID: ${product.id}, adding to invoice")

            // Add product to current invoice
            invoiceViewModel.addToCurrentInvoice(product, 1)

            // Check if product was added to invoice
            val currentInvoiceItems = invoiceViewModel.currentInvoice.value
            Log.d(
                TAG,
                "Invoice items after adding: ${currentInvoiceItems.size}, contains product: ${currentInvoiceItems.any { it.product.id == product.id }}"
            )

            // Navigate to invoice screen
            navController.navigate(Screen.Invoice.route)

            // Clear the scanned product
            homeViewModel.clearScannedProduct()
        }
    }

    // Show toast for price update completion
    priceUpdateMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        // Clear the message after showing
        LaunchedEffect(message) {
            viewModel.clearPriceUpdateMessage()
        }
    }

    // Show toast for stock update completion
    stockUpdateMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        // Clear the message after showing
        LaunchedEffect(message) {
            viewModel.clearStockUpdateMessage()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            FloatingActionButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                    contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode"
                )
            }

            Text(
                str(R.string.welcomeToHomeScreen),
                fontFamily = Kamran,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            )
            // Add Random Products button
            Button(
                onClick = { viewModel.addRandomProducts() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimen(R.dimen.space_14) + 180.dp) // Add more spacing for new buttons
            ) {
                Text("Add Random Products")
            }

            // Set Random Prices button
            Button(
                onClick = { viewModel.setRandomPricesForNullProducts() },
                enabled = !productsLoading && priceUpdateProgress == 0 && stockUpdateProgress == 0,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimen(R.dimen.space_14) + 120.dp) // Add spacing for new button
            ) {
                if (productsLoading && priceUpdateProgress > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Updating...")
                    }
                } else {
                    Text("Set Random Prices")
                }
            }

            // Set Random Stock button
            Button(
                onClick = { viewModel.setRandomStockForAllProducts() },
                enabled = !productsLoading && stockUpdateProgress == 0 && priceUpdateProgress == 0,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimen(R.dimen.space_14) + 60.dp) // Add spacing for scan barcode button
            ) {
                if (productsLoading && stockUpdateProgress > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Updating...")
                    }
                } else {
                    Text("Set Random Stock")
                }
            }
            
            // Scan Barcode button
            Button(
                onClick = { showBarcodeScannerView = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimen(R.dimen.space_14))
            ) {
                Text("Scan Barcode")
            }

            Column(
                modifier = Modifier
                    .padding(bottom = dimen(R.dimen.space_5))
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { homeViewModel.importProducts() }) {
                    Text("Start Import")
                }

                if (progress in 1..99) {
                    LinearProgressIndicator(progress = progress / 100f)
                    Text("در حال وارد کردن: $progress%")
                }

                if (progress == 100) {
                    Text("✅ واردسازی کامل شد!", color = Color.Green)
                }

                // Price update progress indicator
                if (priceUpdateProgress in 1..99) {
                    LinearProgressIndicator(
                        progress = priceUpdateProgress / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "Updating prices: $priceUpdateProgress%",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (priceUpdateProgress == 100) {
                    Text(
                        "✅ Price update completed!",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Auto-hide completion message after 3 seconds
                    LaunchedEffect(priceUpdateProgress) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.clearPriceUpdateMessage()
                    }
                }

                // Stock update progress indicator
                if (stockUpdateProgress in 1..99) {
                    LinearProgressIndicator(
                        progress = stockUpdateProgress / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        "Updating stock: $stockUpdateProgress%",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (stockUpdateProgress == 100) {
                    Text(
                        "✅ Stock update completed!",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Auto-hide completion message after 3 seconds
                    LaunchedEffect(stockUpdateProgress) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.clearStockUpdateMessage()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(dimen(R.dimen.space_4)))

            if (showBottomSheet) {
                ProductSelectionBottomSheet(
                    products = products,
                    onAddToInvoice = { product, quantity ->
                        invoiceViewModel.addToCurrentInvoice(product, quantity)
                        showBottomSheet = false
                    },
                    onDismiss = { showBottomSheet = false }
                )
            }
            
            if (showBarcodeScannerView) {
                BarcodeScannerView(
                    onBarcodeDetected = { barcode ->
                        showBarcodeScannerView = false
                        Log.i(TAG, "Barcode detected: $barcode")
                        // Search for product by barcode
                        homeViewModel.searchProductByBarcode(barcode)
                    },
                    onClose = { showBarcodeScannerView = false }
                )
            }
            
            // Show loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Show error message if any
            errorMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                // Clear error message after showing
                LaunchedEffect(message) {
                    homeViewModel.clearScannedProduct()
                }
            }
        }
    }
    

}

const val TAG = "HomeScreen"

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // We can't provide real viewmodel in preview, so we'll skip it
    // This preview is primarily to check the UI layout
}
