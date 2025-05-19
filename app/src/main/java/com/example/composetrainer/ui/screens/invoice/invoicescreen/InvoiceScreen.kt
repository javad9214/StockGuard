package com.example.composetrainer.ui.screens.invoice.invoicescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.ui.screens.invoice.productselection.AddProductToInvoice
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.utils.FarsiDateUtil
import com.example.composetrainer.ui.components.SetStatusBarColor
import com.example.composetrainer.utils.dimen

@Composable
fun InvoiceScreen(
    onComplete: () -> Unit,
    onClose: () -> Unit,
    viewModel: InvoiceViewModel = hiltViewModel()
) {

    SetStatusBarColor(color = MaterialTheme.colorScheme.background)

    val persianDate = remember { FarsiDateUtil.getTodayPersianDate()}
    val currentTime = remember { FarsiDateUtil.getCurrentTimeFormatted() }
    val nextInvoiceNumber by viewModel.nextInvoiceNumber.collectAsState()
    var showProductSelection by remember { mutableStateOf(false) }
    val products by viewModel.products.collectAsState()
    val invoiceItems by viewModel.currentInvoice.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    LaunchedEffect(key1 = true) {
        viewModel.getNextInvoiceNumberId()
    }

    val totalPrice = invoiceItems.sumOf { it.product.price?.times(it.quantity) ?: 0L }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection(
                invoiceNumber = nextInvoiceNumber?.toInt(),
                persianDate = persianDate.toString(),
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
                        .padding(horizontal = dimen(R.dimen.space_2))
                ) {
                    items(invoiceItems) { item ->
                        InvoiceProductItem(
                            productWithQuantity = item,
                            onRemove = { viewModel.removeFromCurrentInvoice(item.product.id) },
                            onQuantityChange = { newQuantity ->
                                viewModel.updateItemQuantity(item.product.id, newQuantity)
                            }
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
    }
}