package com.example.composetrainer.ui.screens

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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.graphics.Color
import com.example.composetrainer.R
import com.example.composetrainer.ui.screens.invoice.productselection.ProductSelectionBottomSheet
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.theme.Kamran
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.ui.components.BarcodeScannerView
import com.example.composetrainer.ui.viewmodels.HomeViewModel
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.str


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onButtonClick: () -> Unit,
    onToggleTheme: () -> Unit = {},
    isDarkTheme: Boolean = false,
    viewModel: ProductsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val progress by homeViewModel.progress.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBarcodeScannerView by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                    .padding(bottom = dimen(R.dimen.space_14) + 60.dp) // Add spacing for scan barcode button
            ) {
                Text("Add Random Products")
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
                    .fillMaxSize()
                    .padding(16.dp)
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
                    onBarcodeDetected = {
                        showBarcodeScannerView = false
                        Toast.makeText(context, "Barcode: $it", Toast.LENGTH_LONG).show()
                    },
                    onClose = { showBarcodeScannerView = false }
                )
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ComposeTrainerTheme {
        HomeScreen(
            onButtonClick = {},
            onToggleTheme = {},
            isDarkTheme = false
        )
    }
}
