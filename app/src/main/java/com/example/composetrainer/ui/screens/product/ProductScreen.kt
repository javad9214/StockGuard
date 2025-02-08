package com.example.composetrainer.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.ui.viewmodels.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // State for bottom sheet visibility
    val sheetState = rememberModalBottomSheetState()
    val isAddProductSheetOpen = remember { mutableStateOf(false) }


    val selectedProductForEdit = remember { mutableStateOf<Product?>(null) }

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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { isAddProductSheetOpen.value = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {

                TopAppBar(
                    title = { Text("Products") },
                    actions = {
                        SortingDropdown(
                            currentSortOrder = sortOrder,
                            onSortOrderSelected = { viewModel.updateSortOrder(it) }
                        )
                    }
                )
                // SearchBar
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search by name or barcode...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductItem(product = product,
                        onEdit = {
                            // Open Edit Bottom Sheet
                            selectedProductForEdit.value = product
                        },
                        onDelete = {
                            viewModel.deleteProduct(product)
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


//@Preview(showBackground = true)
//@Composable
//fun PreviewProductsScreen() {
//    ComposeTrainerTheme {
//        ProductScreen(viewModel = hiltViewModel())
//    }
//}


