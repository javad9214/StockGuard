package com.example.composetrainer.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.ui.screens.invoice.ProductSelectionBottomSheet
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.utils.dimen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onButtonClick: () -> Unit,
    viewModel: ProductsViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel()
) {

    val products by viewModel.products.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Welcome to Home Screen! ")
        Button(
            onClick = { viewModel.addRandomProducts() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimen(R.dimen.space_14))
        ) {
            Text("Add Random Products")
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
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onButtonClick = {})
}