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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.R
import com.example.composetrainer.ui.screens.invoice.productselection.ProductSelectionBottomSheet
import com.example.composetrainer.ui.theme.Kamran
import com.example.composetrainer.ui.viewmodels.InvoiceListViewModel
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.ui.viewmodels.home.HomeViewModel
import com.example.composetrainer.ui.components.barcodescanner.BarcodeScannerView
import com.example.composetrainer.ui.navigation.Screen
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.ui.viewmodels.SettingViewModel
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.str


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onButtonClick: () -> Unit,
    onToggleTheme: () -> Unit = {},
    isDarkTheme: Boolean = false,
    navController: NavController = rememberNavController(),
    settingViewModel: SettingViewModel = hiltViewModel(),
    productsViewModel: ProductsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel,
    invoiceListViewModel: InvoiceListViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
) {
    val products by productsViewModel.products.collectAsState()
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
    val priceUpdateMessage by settingViewModel.priceUpdateComplete.collectAsState()
    val priceUpdateProgress by settingViewModel.priceUpdateProgress.collectAsState()
    val productsLoading by settingViewModel.isLoading.collectAsState()

    // Observe cost price update completion
    val costPriceUpdateMessage by settingViewModel.costPriceUpdateComplete.collectAsState()
    val costPriceUpdateProgress by settingViewModel.costPriceUpdateProgress.collectAsState()

    
    // Observe stock update completion
    val stockUpdateMessage by settingViewModel.stockUpdateComplete.collectAsState()
    val stockUpdateProgress by settingViewModel.stockUpdateProgress.collectAsState()

    // Observe invoice creation completion
    val invoiceCreationMessage by settingViewModel.invoiceCreationComplete.collectAsState()
    val invoiceCreationProgress by settingViewModel.invoiceCreationProgress.collectAsState()

    // Handle navigation when product is found
    LaunchedEffect(scannedProduct) {
        scannedProduct?.let { product ->
            // Add debug log to verify product is found and being added
            Log.d(
                SETTING_SCREEN_TAG,
                "Product found: ${product.name}, ID: ${product.id}, adding to invoice"
            )

            // Add product to current invoice
            invoiceViewModel.addToCurrentInvoice(product, 1)

            // Check if product was added to invoice
            val currentInvoiceItems = invoiceViewModel.currentInvoice.value


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
            settingViewModel.clearPriceUpdateMessage()
        }
    }

    // Show toast for stock update completion
    stockUpdateMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        // Clear the message after showing
        LaunchedEffect(message) {
            settingViewModel.clearStockUpdateMessage()
        }
    }

    // Show toast for invoice creation completion
    invoiceCreationMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        // Clear the message after showing
        LaunchedEffect(message) {
            settingViewModel.clearInvoiceCreationMessage()
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


            FloatingActionButton(
                onClick = {
                    throw RuntimeException("Test Crash from ACRA!")
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AcUnit,
                    contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode"
                )
            }

            Text(
                str(R.string.welcomeToHomeScreen),
                fontFamily = Kamran,
                modifier = Modifier
                    .align(Alignment.Center)
            )


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
                        settingViewModel.clearPriceUpdateMessage()
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
                        settingViewModel.clearStockUpdateMessage()
                    }
                }

                // Invoice creation progress indicator
                if (invoiceCreationProgress in 1..99) {
                    LinearProgressIndicator(
                        progress = invoiceCreationProgress / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Creating invoices: $invoiceCreationProgress%",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (invoiceCreationProgress == 100) {
                    Text(
                        "✅ Invoice creation completed!",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Auto-hide completion message after 3 seconds
                    LaunchedEffect(invoiceCreationProgress) {
                        kotlinx.coroutines.delay(3000)
                        settingViewModel.clearInvoiceCreationMessage()
                    }
                }
            }

            // Replace the individual buttons with this Column layout
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimen(R.dimen.space_14)),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Add Random Products button
                Button(onClick = { settingViewModel.addRandomProducts() }) {
                    Text("Add Random Products")
                }

                // Set Random Prices button
                Button(
                    onClick = { settingViewModel.setRandomPricesForNullProducts() },
                    enabled = !productsLoading && priceUpdateProgress == 0 && stockUpdateProgress == 0 && invoiceCreationProgress == 0
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

                // Set Random Cost Prices button
                Button(
                    onClick = { settingViewModel.setRandomCostPrice() },
                    enabled = !productsLoading && priceUpdateProgress == 0 && stockUpdateProgress == 0 && invoiceCreationProgress == 0
                ) {
                    Text("Set Random Cost Prices")
                }

                // Set Random Stock button
                Button(
                    onClick = { settingViewModel.setRandomStockForAllProducts() },
                    enabled = !productsLoading && stockUpdateProgress == 0 && priceUpdateProgress == 0 && invoiceCreationProgress == 0
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

                // Create Random Invoices button
                Button(
                    onClick = { settingViewModel.createRandomInvoices() },
                    enabled = !productsLoading && invoiceCreationProgress == 0 && priceUpdateProgress == 0 && stockUpdateProgress == 0
                ) {
                    if (productsLoading && invoiceCreationProgress > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("Creating...")
                        }
                    } else {
                        Text("Create Random Invoices")
                    }
                }

                // Scan Barcode button
                Button(onClick = { showBarcodeScannerView = true }) {
                    Text("Scan Barcode")
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
                        Log.i(SETTING_SCREEN_TAG, "Barcode detected: $barcode")
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

const val SETTING_SCREEN_TAG = "SettingScreen"

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    // We can't provide real viewmodel in preview, so we'll skip it
    // This preview is primarily to check the UI layout
}