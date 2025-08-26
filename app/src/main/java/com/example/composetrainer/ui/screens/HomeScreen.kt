package com.example.composetrainer.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.Kamran
import com.example.composetrainer.ui.viewmodels.InvoiceListViewModel
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.ui.viewmodels.HomeViewModel
import com.example.composetrainer.ui.navigation.Screen
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.utils.dateandtime.FarsiDateUtil
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAlertClick: () -> Unit,
    onButtonClick: () -> Unit,
    onToggleTheme: () -> Unit = {},
    isDarkTheme: Boolean = false,
    navController: NavController = rememberNavController(),
    productViewModel: ProductsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel,
    invoiceListViewModel: InvoiceListViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel()

) {

    val products by productViewModel.products.collectAsState()
    val persianDate = remember { FarsiDateUtil.getTodayFormatted() }
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
    val priceUpdateMessage by productViewModel.priceUpdateComplete.collectAsState()
    val priceUpdateProgress by productViewModel.priceUpdateProgress.collectAsState()
    val productsLoading by productViewModel.isLoading.collectAsState()

    // Observe stock update completion
    val stockUpdateMessage by productViewModel.stockUpdateComplete.collectAsState()
    val stockUpdateProgress by productViewModel.stockUpdateProgress.collectAsState()

    // Observe invoice creation completion
    val invoiceCreationMessage by productViewModel.invoiceCreationComplete.collectAsState()
    val invoiceCreationProgress by productViewModel.invoiceCreationProgress.collectAsState()

    // Handle navigation when product is found
    LaunchedEffect(scannedProduct) {
        scannedProduct?.let { product ->
            // Add debug log to verify product is found and being added
            Log.d(TAG, "Product found: ${product.name}, ID: ${product.id}, adding to invoice")

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
                    text = persianDate,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_xl)
                )


                IconButton(onClick = onAlertClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.notifications_24px),
                        contentDescription = "Notifications"
                    )
                }


            }

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                // Settings FAB
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.Settings.route) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Navigate to Settings"
                    )
                }

            }
        }
    }
}

const val TAG = "HomeScreen"

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // We can't provide real viewmodel in preview, so we'll skip it
    // This preview is primarily to check the UI layout
}
