package com.example.composetrainer.ui.screens.productlist.serverlist

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.ui.components.barcodescanner.BarcodeScannerView
import com.example.composetrainer.ui.screens.component.EmptyState
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.MainProductsViewModel
import com.example.composetrainer.utils.barcode.BarcodeSoundPlayer
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun ServerProductListScreen(
    onNavigateBack: () -> Unit,
    context: Context = LocalContext.current,
    mainProductsViewModel: MainProductsViewModel = hiltViewModel()
) {

    val searchQuery by mainProductsViewModel.searchQuery.collectAsState()
    val uiState by mainProductsViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    // Barcode scanner state
    var showBarcodeScannerView by remember { mutableStateOf(false) }


    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastVisibleIndex ->
            if (lastVisibleIndex != null &&
                lastVisibleIndex >= uiState.products.size - 3 &&
                uiState.hasMorePages &&
                !uiState.isLoadingMore
            ) {
                mainProductsViewModel.loadMoreProducts()
            }
        }
    }

    // Show barcode scanner when activated - moved outside to fix layering
    if (showBarcodeScannerView) {
        BarcodeScannerView(
            onBarcodeDetected = { barcode ->
                showBarcodeScannerView = false

                // Set the search query to the scanned barcode
                mainProductsViewModel.updateSearchQuery(barcode)
                // Play barcode success sound
                BarcodeSoundPlayer.playBarcodeSuccessSound(context)

            },
            onClose = {
                showBarcodeScannerView = false
            }
        )
    } else {
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
                    str(R.string.total_products_list),
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_xl)
                )

                IconButton(onClick = onNavigateBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                        contentDescription = str(R.string.back)
                    )
                }

            }

            // SearchBar with barcode scan button
            TextField(
                value = searchQuery,
                onValueChange = { query -> mainProductsViewModel.updateSearchQuery(query) },
                trailingIcon = {
                    Row {
                        // Barcode scanner button
                        IconButton(onClick = { showBarcodeScannerView = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.barcode_scanner_24px),
                                contentDescription = "Scan Barcode"
                            )
                        }

                        // Clear button
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { mainProductsViewModel.updateSearchQuery("") }) {
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

            // Show error snackbar
            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    // Show snackbar or handle error UI
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.products.isEmpty() -> {
                        // Initial loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.products.isEmpty() && !uiState.isLoading -> {
                        // Empty state
                        EmptyState(
                            message = str(R.string.no_product_found),
                            onRetry = { mainProductsViewModel.retry() }
                        )
                    }

                    else -> {
                        // Products list
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(dimen(R.dimen.space_2)),
                            verticalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))
                        ) {
                            items(
                                items = uiState.products,
                                key = { product -> product.id.value }
                            ) { product ->
                                ServerProductItem(
                                    product = product,
                                    onProductClick = { },
                                    onAdd = {
                                        mainProductsViewModel.addProductToLocalDatabase(
                                            product
                                        )
                                    },
                                    onEdit = { },
                                    onDelete = { },
                                    )
                            }

                            // Loading more indicator
                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }


}