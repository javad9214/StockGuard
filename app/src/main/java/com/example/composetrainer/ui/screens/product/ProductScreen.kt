package com.example.composetrainer.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.viewmodels.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // State for bottom sheet visibility
    val sheetState = rememberModalBottomSheetState()
    val isAddProductSheetOpen = remember { mutableStateOf(false) }

    if (isAddProductSheetOpen.value) {
        ModalBottomSheet(
            onDismissRequest = { isAddProductSheetOpen.value = false },
            sheetState = sheetState
        ) {
            AddProductBottomSheet(
                onAddProduct = { product ->
                    viewModel.addProduct(product)
                    isAddProductSheetOpen.value = false
                },
                onDismiss = { isAddProductSheetOpen.value = false }
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { isAddProductSheetOpen.value = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
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
                    ProductItem(product = product)
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewProductsScreen() {
    ComposeTrainerTheme {
        ProductScreen(viewModel = hiltViewModel())
    }
}


