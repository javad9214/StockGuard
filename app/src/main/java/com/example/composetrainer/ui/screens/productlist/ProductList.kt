package com.example.composetrainer.ui.screens.productlist

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Barcode
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductFactory
import com.example.composetrainer.domain.model.ProductName
import com.example.composetrainer.ui.components.barcodescanner.BarcodeScannerView
import com.example.composetrainer.ui.components.util.ConfirmyHost
import com.example.composetrainer.ui.components.util.ConfirmyType
import com.example.composetrainer.ui.components.util.SnackyHost
import com.example.composetrainer.ui.components.util.SnackyType
import com.example.composetrainer.ui.components.util.rememberConfirmyHostState
import com.example.composetrainer.ui.components.util.rememberSnackyHostState
import com.example.composetrainer.ui.navigation.Screen
import com.example.composetrainer.ui.screens.component.NoBarcodeFoundDialog
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.MainProductsViewModel
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.ui.viewmodels.SortOrder
import com.example.composetrainer.ui.viewmodels.home.HomeViewModel
import com.example.composetrainer.utils.barcode.BarcodeSoundPlayer
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    productsViewModel: ProductsViewModel = hiltViewModel(),
    mainProductsViewModel: MainProductsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val products by productsViewModel.products.collectAsState()
    val isLoading by productsViewModel.isLoading.collectAsState()
    val sortOrder by productsViewModel.sortOrder.collectAsState()
    val searchQuery by productsViewModel.searchQuery.collectAsState()

    // Observe scanned product from HomeViewModel for barcode scanning
    val scannedProduct by homeViewModel.scannedProduct.collectAsState()
    val scannerIsLoading by homeViewModel.isLoading.collectAsState()
    val scannerErrorMessage by homeViewModel.errorMessage.collectAsState()
    val scannedBarcode by homeViewModel.detectedBarcode.collectAsState()
    val noBarcodeFoundDialogSheetState = rememberModalBottomSheetState()
    var showNoBarcodeFoundDialog by remember { mutableStateOf(false) }
    // Context for MediaPlayer
    val context = LocalContext.current

    // State for bottom sheet visibility
    val sheetState = rememberModalBottomSheetState()
    // for add new product Bottom Sheet
    val addNewProductSheetState = rememberModalBottomSheetState()
    val isAddProductSheetOpen = remember { mutableStateOf(false) }
    val selectedProductForEdit = remember { mutableStateOf<Product?>(null) }

    // Barcode scanner state
    var showBarcodeScannerView by remember { mutableStateOf(false) }

    var productToDelete by remember { mutableStateOf<Product?>(null) }

    if (selectedProductForEdit.value != null || isAddProductSheetOpen.value) {
        ModalBottomSheet(
            onDismissRequest = { isAddProductSheetOpen.value = false },
            containerColor = MaterialTheme.colorScheme.background,
            sheetState = sheetState
        ) {
            AddProductBottomSheet(
                initialProduct = selectedProductForEdit.value?.copy()
                    ?: if (scannedBarcode.isNullOrEmpty().not())
                        ProductFactory.createBasic(
                            name = ProductName(""),
                            barcode = Barcode(scannedBarcode!!),
                            price = 0,
                            costPrice = 0,
                            initialStock = 0
                        )
                    else
                        null,
                onSave = { product ->
                    if (selectedProductForEdit.value == null) {
                        productsViewModel.addProduct(product)
                        mainProductsViewModel.addNewProductToRemote(product)
                    } else {
                        productsViewModel.editProduct(product)
                    }
                    selectedProductForEdit.value = null
                    isAddProductSheetOpen.value = false
                    homeViewModel.clearDetectedBarcode()
                },
                onDismiss = {
                    selectedProductForEdit.value = null
                    isAddProductSheetOpen.value = false
                    homeViewModel.clearDetectedBarcode()
                }
            )
        }
    }

    // Handle when a product is found by barcode
    LaunchedEffect(scannedProduct) {
        scannedProduct?.let { _ ->
            // Add product to invoice
            productsViewModel.updateSearchQuery(scannedBarcode?:"")
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

    } else {

        ProductScreenContent(
            products = products,
            isLoading = isLoading,
            sortOrder = sortOrder,
            searchQuery = searchQuery,
            onSearchQueryChange = { productsViewModel.updateSearchQuery(it) },
            onSortOrderSelected = { productsViewModel.updateSortOrder(it) },
            onAddProduct = { isAddProductSheetOpen.value = true },
            onEditProduct = { selectedProductForEdit.value = it },
            onDisableProduct = {  }, // TODO: Implement disable product functionality
            onDeleteProduct = { productToDelete = it },
            onIncreaseStock = { productsViewModel.increaseStock(it) },
            onDecreaseStock = { productsViewModel.decreaseStock(it) },
            onScanBarcode = { showBarcodeScannerView = true },
            navController = navController
        )
    }

    // Show confirmation dialog for product deletion
    productToDelete?.let { product ->
        ShowConfirmy(
            productsViewModel = productsViewModel,
            product = product,
            onFinish = { productToDelete = null }
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
@Composable
fun ShowConfirmy(
    productsViewModel: ProductsViewModel,
    product: Product,
    onFinish: () -> Unit
){
    val confirmyHostState = rememberConfirmyHostState()
    val snackyHostState = rememberSnackyHostState()
    val scope = rememberCoroutineScope()

    val deleteMessage = stringResource(R.string.product_is_deleted_successfully)

    Log.d("ProductList", "ShowConfirmy: $product")

    confirmyHostState.show(
        message = str(R.string.are_you_sure_you_want_to_delete_this_product),
        type = ConfirmyType.ERROR,
        confirmText = str(R.string.delete),
        cancelText = str(R.string.cancel),
        onConfirm = {
            scope.launch {
                productsViewModel.deleteProduct(product)
                snackyHostState.show(
                    message = deleteMessage,
                    type = SnackyType.INFO
                )
                onFinish()
            }
        },
        onCancel = {
            onFinish()
           // nothing happen
        }
    )

    ConfirmyHost(hostState = confirmyHostState)
    SnackyHost(hostState = snackyHostState)
}

@Composable
fun ProductScreenContent(
    products: List<Product>,
    isLoading: Boolean,
    sortOrder: SortOrder,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSortOrderSelected: (SortOrder) -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onDisableProduct: (Product) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    onIncreaseStock: (Product) -> Unit,
    onDecreaseStock: (Product) -> Unit,
    navController: NavController,
    onScanBarcode: () -> Unit = {}
) {

    Column {

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    str(R.string.myProducts),
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_xl)
                )

                Row {

                    IconButton(onClick = { navController.navigate(Screen.MainServerProductLiat.route) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.receive_square_01),
                            contentDescription = "Navigate to Main Server Products "
                        )
                    }

                    IconButton(onClick = onAddProduct) {
                        Icon(Icons.Default.Add, contentDescription = "Add Product")
                    }

                    IconButton(onClick = { onSortOrderSelected(sortOrder) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort"
                        )
                    }
                }


            }

            // SearchBar with barcode scan button
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                trailingIcon = {
                    Row {
                        // Barcode scanner button
                        IconButton(onClick = onScanBarcode) {
                            Icon(
                                painter = painterResource(id = R.drawable.barcode_scanner_24px),
                                contentDescription = "Scan Barcode"
                            )
                        }

                        // Clear button
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimen(R.dimen.space_4),
                        vertical = dimen(R.dimen.space_2)
                    ),
                placeholder = {
                    Text(
                        str(R.string.search_products),
                        fontFamily = BMitra
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onEdit = { onEditProduct(product) },
                        onDisable = { onDisableProduct(product) },
                        onDelete = { onDeleteProduct(product) },
                        onProductClick = {
                           // navController.navigate(Screen.ProductDetails.createRoute(product.id.value))
                        }
                    )
                }
            }

        }

    }

}

@Composable
fun SortingDropdown(
    currentSortOrder: SortOrder,
    onSortOrderSelected: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Newest First") },
                onClick = {
                    onSortOrderSelected(SortOrder.DESCENDING)
                    expanded = false
                },
                trailingIcon = {
                    if (currentSortOrder == SortOrder.DESCENDING) {
                        Icon(Icons.Default.Check, contentDescription = "Selected")
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Oldest First") },
                onClick = {
                    onSortOrderSelected(SortOrder.ASCENDING)
                    expanded = false
                },
                trailingIcon = {
                    if (currentSortOrder == SortOrder.ASCENDING) {
                        Icon(Icons.Default.Check, contentDescription = "Selected")
                    }
                }
            )
        }
    }
}
