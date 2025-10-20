package com.example.composetrainer.ui.screens.invoice.invoicescreen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Barcode
import com.example.composetrainer.domain.model.ProductFactory
import com.example.composetrainer.domain.model.ProductName
import com.example.composetrainer.domain.model.calculateTotalAmount
import com.example.composetrainer.domain.model.hasProducts
import com.example.composetrainer.ui.components.barcodescanner.BarcodeScannerView
import com.example.composetrainer.ui.screens.component.NoBarcodeFoundDialog
import com.example.composetrainer.ui.screens.invoice.productselection.AddProductToInvoice
import com.example.composetrainer.ui.screens.productlist.AddProductBottomSheet
import com.example.composetrainer.ui.viewmodels.InvoiceListViewModel
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.ui.viewmodels.home.HomeViewModel
import com.example.composetrainer.utils.barcode.BarcodeSoundPlayer
import com.example.composetrainer.utils.dateandtime.FarsiDateUtil
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.str

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    onComplete: () -> Unit,
    onClose: () -> Unit,
    invoiceListViewModel: InvoiceListViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
    productsViewModel: ProductsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val persianDate = remember { FarsiDateUtil.getTodayPersianDate() }
    val currentTime = remember { FarsiDateUtil.getCurrentTimeFormatted() }
    var showProductSelection by remember { mutableStateOf(false) }
    var showBarcodeScannerView by remember { mutableStateOf(false) }
    val products by productsViewModel.products.collectAsState()
    val currentInvoice by invoiceViewModel.currentInvoice.collectAsState()
    val isLoading by invoiceListViewModel.isLoading.collectAsState()

    // Observe scanned product from HomeViewModel for barcode scanning
    val scannedProduct by homeViewModel.scannedProduct.collectAsState()
    val scannerIsLoading by homeViewModel.isLoading.collectAsState()
    val scannerErrorMessage by homeViewModel.errorMessage.collectAsState()
    val scannedBarcode by homeViewModel.detectedBarcode.collectAsState()
    val noBarcodeFoundDialogSheetState = rememberModalBottomSheetState()
    var showNoBarcodeFoundDialog by remember { mutableStateOf(false) }

    // for add new product Bottom Sheet
    val addNewProductSheetState = rememberModalBottomSheetState()
    val isAddProductSheetOpen = remember { mutableStateOf(false) }

    // Context for MediaPlayer
    val context = LocalContext.current

    val TAG = "InvoiceScreen"

    // Handle when a product is found by barcode
    LaunchedEffect(scannedProduct) {
        scannedProduct?.let { product ->
            // Add product to invoice
            invoiceViewModel.addToCurrentInvoice(product, 1)
            Log.d("InvoiceScreen", "Added product from barcode: ${product.name}")
            // Clear scanned product
            homeViewModel.clearScannedProduct()
        }
    }

    LaunchedEffect(scannerErrorMessage) {
        if (scannerErrorMessage != null && scannedBarcode != null) {
            showNoBarcodeFoundDialog = true
            noBarcodeFoundDialogSheetState.show()
        }
    }

    if (showNoBarcodeFoundDialog) {

        NoBarcodeFoundDialog(
            barcode = scannedBarcode!!,
            sheetState = noBarcodeFoundDialogSheetState,
            onAddToNewProductClicked = {
                showNoBarcodeFoundDialog = false
                isAddProductSheetOpen.value = true
            },
            onDismiss = {
                showNoBarcodeFoundDialog = false
                homeViewModel.clearErrorMessage()
            }
        )

    }

    if (isAddProductSheetOpen.value) {
        ModalBottomSheet(
            onDismissRequest = { isAddProductSheetOpen.value = false },
            sheetState = addNewProductSheetState
        ) {
            AddProductBottomSheet(
                initialProduct = null,
                onSave = { product ->
                    productsViewModel.addProduct(product)
                    isAddProductSheetOpen.value = false

                },
                onDismiss = {
                    isAddProductSheetOpen.value = false
                }
            )
        }
    }

    val totalPrice = currentInvoice.calculateTotalAmount()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection(
                invoiceNumber = currentInvoice.invoice.invoiceNumber.value.toString(),
                persianDate = persianDate,
                currentTime = currentTime,
                onAddProductClick = { showProductSelection = true },
                onClose = onClose,
                onScanBarcodeClick = { showBarcodeScannerView = true },
                onInvoiceTypeChange = { invoiceType ->
                    invoiceViewModel.changeInvoiceType(invoiceType)
                }
            )

            // Products list section
            if (currentInvoice.hasProducts()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = dimen(R.dimen.space_2)),
                    contentPadding = PaddingValues(bottom = dimen(R.dimen.space_14))

                ) {

                    items(
                        currentInvoice.totalProductsCount,
                        key = { currentInvoice.products[it].id.value }) { item ->
                        InvoiceProductItem(
                            productWithQuantity = currentInvoice.invoiceProducts[item],
                            product = currentInvoice.products[item],
                            onRemove = { invoiceViewModel.removeFromCurrentInvoice(currentInvoice.products[item].id) },
                            onQuantityChange = { newQuantity ->
                                invoiceViewModel.updateItemQuantity(
                                    currentInvoice.products[item].id.value,
                                    newQuantity
                                )
                            }
                        )

                    }
                }
            } else {
                // Show empty state when no products added
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = str(R.string.no_products_added),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            // Bottom total section
            BottomTotalSection(
                totalPrice = totalPrice.amount,
                isLoading = isLoading,
                hasItems = currentInvoice.products.isNotEmpty(),
                onSubmit = {
                    if (currentInvoice.isValid()) {
                        invoiceViewModel.saveInvoice()
                        onComplete()
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }


        AnimatedVisibility(
            visible = showProductSelection,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 600)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 600)
            )
        ) {
            AddProductToInvoice(
                onClose = { showProductSelection = false }
            )
        }

        // Show barcode scanner when activated
        if (showBarcodeScannerView) {
            BarcodeScannerView(
                onBarcodeDetected = { barcode ->
                    showBarcodeScannerView = false
                    Log.d("InvoiceScreen", "Barcode detected: $barcode")
                    // Play barcode success sound
                    BarcodeSoundPlayer.playBarcodeSuccessSound(context)

                    homeViewModel.searchProductByBarcode(barcode)
                },
                onClose = { showBarcodeScannerView = false }
            )

        }

        // Show loading indicator for barcode scanning
        if (scannerIsLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

}