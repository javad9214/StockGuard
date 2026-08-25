package ir.yar.anbar.ui.screens.invoice.invoicescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.InvoiceType
import ir.yar.anbar.domain.model.calculateTotalAmount
import ir.yar.anbar.domain.model.calculateTotalCost
import ir.yar.anbar.domain.model.hasProducts
import ir.yar.anbar.ui.components.barcodescanner.CompactBarcodeScanner
import ir.yar.anbar.ui.components.util.SnackyDuration
import ir.yar.anbar.ui.components.util.SnackyHost
import ir.yar.anbar.ui.components.util.SnackyType
import ir.yar.anbar.ui.components.util.rememberSnackyHostState
import ir.yar.anbar.ui.screens.component.NoBarcodeFoundDialog
import ir.yar.anbar.ui.screens.invoice.productselection.AddProductToInvoice
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.viewmodels.InvoiceViewModel
import ir.yar.anbar.ui.viewmodels.InvoiceViewModel.InvoiceEvent
import ir.yar.anbar.ui.viewmodels.home.HomeViewModel
import ir.yar.anbar.utils.barcode.BarcodeSoundPlayer
import ir.yar.anbar.utils.dateandtime.FarsiDateUtil
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str
import kotlinx.coroutines.delay

/** How long the success snackbar stays visible before navigating back. */
private const val SUCCESS_SNACKBAR_DURATION_MS = 500L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    onComplete: () -> Unit,
    onClose: () -> Unit,
    onAddNewProduct: (barcode: String) -> Unit,
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val persianDate = remember { FarsiDateUtil.getTodayPersianDate() }
    val currentTime = remember { FarsiDateUtil.getCurrentTimeFormatted() }
    var showProductSelection by remember { mutableStateOf(false) }
    val uiState by invoiceViewModel.uiState.collectAsState()

    val snackyHostState = rememberSnackyHostState()
    val loadingSaveInvoiceMessage = str(R.string.finalizing_invoice)
    val successSaveInvoiceMessage = str(R.string.invoice_created_successfully)

    // Observe barcode-scan state from HomeViewModel — one snapshot, so error and
    // barcode can never come from different scans
    val scanState by homeViewModel.uiState.collectAsState()
    val scannedProduct = scanState.scannedProduct
    val scannerIsLoading = scanState.isLoading
    val scannerErrorMessage = scanState.errorMessage
    val scannedBarcode = scanState.detectedBarcode
    val noBarcodeFoundDialogSheetState = rememberModalBottomSheetState()
    var showNoBarcodeFoundDialog by remember { mutableStateOf(false) }

    // Context for MediaPlayer
    val context = LocalContext.current

    // Handle when a product is found by barcode
    LaunchedEffect(scannedProduct) {
        scannedProduct?.let { product ->
            invoiceViewModel.addToCurrentInvoice(product, quantity = 1)
            homeViewModel.clearScannedProduct()
        }
    }

    LaunchedEffect(scannerErrorMessage, scannedBarcode) {
        if (scannerErrorMessage != null && scannedBarcode != null) {
            showNoBarcodeFoundDialog = true
            noBarcodeFoundDialogSheetState.show()
        }
    }

    LaunchedEffect(Unit) {
        invoiceViewModel.events.collect { event ->
            when (event) {
                InvoiceEvent.SaveSuccess -> {
                    snackyHostState.show(
                        message = successSaveInvoiceMessage,
                        type = SnackyType.SUCCESS,
                        duration = SnackyDuration.SHORT
                    )

                    delay(SUCCESS_SNACKBAR_DURATION_MS)
                    onComplete()
                }
            }
        }
    }

    // Saving indicator driven by state, so it can't get out of sync with the UI
    LaunchedEffect(uiState.isSaving) {
        if (uiState.isSaving) {
            snackyHostState.show(
                message = loadingSaveInvoiceMessage,
                type = SnackyType.LOADING
            )
        } else {
            snackyHostState.dismiss()
        }
    }

    // Surface state errors (init/save failures) as dismissible snackbars
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackyHostState.show(
                message = message,
                type = SnackyType.ERROR,
                duration = SnackyDuration.LONG
            )
            invoiceViewModel.clearError()
        }
    }

    // Render the dialog only while both values exist — never force-unwrap scan state
    if (showNoBarcodeFoundDialog && scannedBarcode != null && scannerErrorMessage != null) {
        NoBarcodeFoundDialog(
            barcode = scannedBarcode,
            sheetState = noBarcodeFoundDialogSheetState,
            onAddToNewProductClicked = {
                showNoBarcodeFoundDialog = false
                onAddNewProduct(scannedBarcode)
            },
            onDismiss = {
                showNoBarcodeFoundDialog = false
                homeViewModel.clearErrorMessage()
            }
        )
    }

    // Pair each invoice line with its product by id instead of trusting two
    // parallel lists to stay index-aligned
    val lineItems = uiState.currentInvoice.invoiceProducts.mapNotNull { line ->
        uiState.currentInvoice.products
            .find { product -> product.id == line.productId }
            ?.let { product -> line to product }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection(
                invoiceNumber = uiState.currentInvoice.invoiceNumber.value.toString(),
                persianDate = persianDate,
                currentTime = currentTime,
                onClose = onClose,
                onInvoiceTypeChange = { invoiceType ->
                    invoiceViewModel.changeInvoiceType(invoiceType)
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 182.dp
                    )
            ) {
                CompactBarcodeScanner(
                    onBarcodeDetected = { barcode ->

                        // Play barcode success sound
                        BarcodeSoundPlayer.playBarcodeSuccessSound(context)

                        homeViewModel.searchProductByBarcode(barcode)
                    },
                    startPaused = true,
                    modifier = Modifier
                        .padding(
                            horizontal = dimen(R.dimen.space_2),
                            vertical = dimen(R.dimen.space_2)
                        )
                        .align(Alignment.TopCenter),
                    cardRadius = dimen(R.dimen.radius_md)
                )

                OutlinedButton(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = dimen(R.dimen.space_4)),
                    onClick = { showProductSelection = true },
                    shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                    contentPadding = PaddingValues(
                        vertical = dimen(R.dimen.space_1),
                        horizontal = dimen(R.dimen.space_3)
                    ),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = str(R.string.add),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = str(R.string.choose_from_list),
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_sm)
                    )
                }

            }

            // Products list section
            if (uiState.currentInvoice.hasProducts()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = dimen(R.dimen.space_2)),
                    contentPadding = PaddingValues(bottom = dimen(R.dimen.space_2))

                ) {

                    items(
                        lineItems,
                        key = { (line, _) -> line.productId.value }
                    ) { (line, product) ->
                        InvoiceProductItem(
                            productWithQuantity = line,
                            product = product,
                            onRemove = { invoiceViewModel.removeFromCurrentInvoice(product.id) },
                            onQuantityChange = { newQuantity ->
                                invoiceViewModel.updateItemQuantity(product.id, newQuantity)
                            },
                            invoiceType = uiState.currentInvoice.invoice.invoiceType
                                ?: InvoiceType.SALE
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
                        fontFamily = Beirut_Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Footer pinned to the bottom of the screen; the items list scrolls above it
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                BottomTotalSection(
                    totalPrice = if (uiState.currentInvoice.invoice.invoiceType == InvoiceType.SALE) {
                        uiState.currentInvoice.calculateTotalAmount().amount
                    } else {
                        uiState.currentInvoice.calculateTotalCost().amount
                    },
                    isLoading = uiState.isLoading,
                    hasItems = uiState.currentInvoice.products.isNotEmpty(),
                    onSubmit = {
                        val invoice = uiState.currentInvoice
                        if (invoice.hasProducts() && invoice.isValid()) {
                            invoiceViewModel.saveInvoice()
                        }
                    }
                )
            }
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

    SnackyHost(hostState = snackyHostState)
}
