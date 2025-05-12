package com.example.composetrainer.ui.screens.invoice.invoicescreen

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.ui.screens.invoice.ProductSelectionBottomSheet
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.utils.DateFormatter
import com.example.composetrainer.utils.dimen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    onComplete: () -> Unit,
    onClose: () -> Unit,
    viewModel: InvoiceViewModel = hiltViewModel()
) {
    val persianDate = remember { DateFormatter.getFormattedHijriShamsiDate() }
    val currentTime = remember { DateFormatter.getCurrentTimeFormatted() }
    val nextInvoiceNumber by viewModel.nextInvoiceNumber.collectAsState()
    var showProductSelection by remember { mutableStateOf(false) }
    val products by viewModel.products.collectAsState()
    val invoiceItems by viewModel.currentInvoice.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val view = LocalView.current
    val context = LocalContext.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as Activity).window
            window.statusBarColor = ContextCompat.getColor(context, R.color.white)
            window.navigationBarColor = ContextCompat.getColor(context, R.color.white)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.getNextInvoiceNumberId()
    }

    val totalPrice = invoiceItems.sumOf { it.product.price?.times(it.quantity) ?: 0L }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection(
                invoiceNumber = nextInvoiceNumber?.toInt(),
                persianDate = persianDate,
                currentTime = currentTime,
                onAddProductClick = { showProductSelection = true },
                onClose = onClose
            )

            // Products list section
            if (invoiceItems.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = dimen(R.dimen.space_4))
                ) {
                    items(invoiceItems) { item ->
                        InvoiceProductItem(
                            productWithQuantity = item,
                            onRemove = { viewModel.removeFromCurrentInvoice(item.product.id) }
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
                        text = "No products added yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        }

        // Bottom total section
        BottomTotalSection(
            totalPrice = totalPrice,
            isLoading = isLoading,
            hasItems = invoiceItems.isNotEmpty(),
            onSubmit = {
                if (invoiceItems.isNotEmpty()) {
                    viewModel.createInvoice()
                    onComplete()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (showProductSelection) {
            ProductSelectionBottomSheet(
                products = products,
                onAddToInvoice = { product, quantity ->
                    viewModel.addToCurrentInvoice(product, quantity)
                },
                onDismiss = { showProductSelection = false }
            )
        }
    }
}