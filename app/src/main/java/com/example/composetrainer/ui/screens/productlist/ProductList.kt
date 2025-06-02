package com.example.composetrainer.ui.screens.productlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.ui.components.BarcodeScannerView
import com.example.composetrainer.ui.navigation.Screen
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.ui.viewmodels.SortOrder
import com.example.composetrainer.utils.str
import com.example.composetrainer.utils.BarcodeSoundPlayer
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Context for MediaPlayer
    val context = LocalContext.current

    // State for bottom sheet visibility
    val sheetState = rememberModalBottomSheetState()
    val isAddProductSheetOpen = remember { mutableStateOf(false) }
    val selectedProductForEdit = remember { mutableStateOf<Product?>(null) }

    // Barcode scanner state
    var showBarcodeScannerView by remember { mutableStateOf(false) }

    if (selectedProductForEdit.value != null || isAddProductSheetOpen.value) {
        ModalBottomSheet(
            onDismissRequest = { isAddProductSheetOpen.value = false },
            sheetState = sheetState
        ) {
            AddProductBottomSheet(
                onSave = { product ->
                    if (selectedProductForEdit.value == null) {
                        viewModel.addProduct(product)
                    } else {
                        viewModel.editProduct(product)
                    }
                    selectedProductForEdit.value = null
                    isAddProductSheetOpen.value = false
                },
                onDismiss = {
                    selectedProductForEdit.value = null
                    isAddProductSheetOpen.value = false
                }
            )
        }
    }

    // Show barcode scanner when activated - moved outside to fix layering
    if (showBarcodeScannerView) {
        BarcodeScannerView(
            onBarcodeDetected = { barcode ->
                showBarcodeScannerView = false
                // Set the search query to the scanned barcode
                viewModel.updateSearchQuery(barcode)

                // Play barcode success sound
                BarcodeSoundPlayer.playBarcodeSuccessSound(context)
            },
            onClose = {
                showBarcodeScannerView = false
            }
        )
    } else {
        ProductScreenContent(
            products = products,
            isLoading = isLoading,
            sortOrder = sortOrder,
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
            onSortOrderSelected = { viewModel.updateSortOrder(it) },
            onAddProduct = { isAddProductSheetOpen.value = true },
            onEditProduct = { selectedProductForEdit.value = it },
            onDeleteProduct = { viewModel.deleteProduct(it) },
            onIncreaseStock = { viewModel.increaseStock(it) },
            onDecreaseStock = { viewModel.decreaseStock(it) },
            navController = navController,
            onScanBarcode = { showBarcodeScannerView = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onDeleteProduct: (Product) -> Unit,
    onIncreaseStock: (Product) -> Unit,
    onDecreaseStock: (Product) -> Unit,
    navController: NavController? = null,
    onScanBarcode: () -> Unit = {}
) {

    Column {

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    str(R.string.products),
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_xl)
                )

                IconButton(onClick = { onSortOrderSelected }) {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = "Sort"
                    )
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
                        onDelete = { onDeleteProduct(product) },
                        onIncreaseStock = { onIncreaseStock(product) },
                        onDecreaseStock = { onDecreaseStock(product) },
                        onProductClick = {
                            navController?.navigate(Screen.ProductDetails.createRoute(product.id))
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = onAddProduct,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
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

@Preview(showBackground = true)
@Composable
fun PreviewProductsScreen() {
    val mockProducts = listOf(
        Product(
            id = 1L,
            name = "Laptop",
            barcode = "123456789",
            price = 999L,
            stock = 5,
            image = null,
            subCategoryId = 1,
            date = System.currentTimeMillis()
        ),
        Product(
            id = 2L,
            name = "Phone",
            barcode = "987654321",
            price = 499L,
            stock = 10,
            image = null,
            subCategoryId = 1,
            date = System.currentTimeMillis()
        ),
        Product(
            id = 3L,
            name = "Headphones",
            barcode = "456789123",
            price = 79L,
            stock = 20,
            image = null,
            subCategoryId = 2,
            date = System.currentTimeMillis()
        )
    )

    ComposeTrainerTheme {
        ProductScreenContent(
            products = mockProducts,
            isLoading = false,
            sortOrder = SortOrder.DESCENDING,
            searchQuery = "",
            onSearchQueryChange = {},
            onSortOrderSelected = {},
            onAddProduct = {},
            onEditProduct = {},
            onDeleteProduct = {},
            onIncreaseStock = {},
            onDecreaseStock = {},
            navController = null
        )
    }
}