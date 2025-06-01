package com.example.composetrainer.ui.screens.invoicelist

import android.app.AlertDialog as AndroidAlertDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.ui.theme.BComps
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesListScreen(
    viewModel: InvoiceViewModel = hiltViewModel(),
    onCreateNew: () -> Unit,
    onInvoiceClick: (Long) -> Unit
) {
    val invoices by viewModel.invoices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val sortNewestFirst by viewModel.sortNewestFirst.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedInvoices by viewModel.selectedInvoices.collectAsState()
    val showDeleteConfirmationDialog by viewModel.showDeleteConfirmationDialog.collectAsState()

    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Column(
            modifier = Modifier
                .fillMaxSize()// or use WindowInsets.statusBars.asPaddingValues()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (isSelectionMode) {
                    Text(
                        text = if (selectedInvoices.isEmpty()) str(R.string.select_invoices_to_delete)
                        else str(R.string.selected_invoices_count).format(selectedInvoices.size),
                        fontFamily = BComps,
                        fontSize = dimenTextSize(R.dimen.text_size_lg)
                    )

                    Row {
                        // Delete button
                        IconButton(
                            onClick = {
                                if (selectedInvoices.isNotEmpty()) {
                                    viewModel.showDeleteConfirmationDialog()
                                }
                            },
                            enabled = selectedInvoices.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = str(R.string.delete),
                                tint = if (selectedInvoices.isEmpty())
                                    Color.Gray else MaterialTheme.colorScheme.error
                            )
                        }

                        // Cancel selection mode
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = str(R.string.cancel)
                            )
                        }
                    }
                } else {
                    Text(
                        str(R.string.sale_invoices),
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_xl)
                    )

                    Row {
                        // Long press hint
                        if (invoices.isNotEmpty()) {
                            IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = str(R.string.selection_mode)
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.toggleSortOrder() }) {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = if (sortNewestFirst)
                                    "Sort oldest to newest" else "Sort newest to oldest"
                            )
                        }
                    }
                }


            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                    errorMessage != null -> Toast.makeText(
                        context,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    invoices.isEmpty() -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No invoices available",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    else -> InvoicesLazyList(
                        invoices = invoices,
                        onInvoiceClick = {
                            if (isSelectionMode) {
                                viewModel.toggleInvoiceSelection(it)
                            } else {
                                onInvoiceClick(it)
                            }
                        },
                        onDelete = viewModel::deleteInvoice,
                        onLongClick = {
                            if (!isSelectionMode) {
                                viewModel.toggleSelectionMode()
                                viewModel.toggleInvoiceSelection(it)
                            }
                        },
                        isSelectionMode = isSelectionMode,
                        selectedInvoices = selectedInvoices
                    )

                }
            }
        }


    }

    // Show delete confirmation dialog when needed
    if (showDeleteConfirmationDialog) {
        val context = LocalContext.current
        AndroidAlertDialog.Builder(context)
            .setTitle(context.getString(R.string.delete_selected_invoices))
            .setMessage(context.getString(R.string.are_you_sure_to_delete_selected_invoices))
            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                viewModel.deleteSelectedInvoices()
            }
            .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
                viewModel.dismissDeleteConfirmationDialog()
            }
            .show()
    }
}

@Composable
private fun InvoicesLazyList(
    invoices: List<Invoice>,
    onInvoiceClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onLongClick: (Long) -> Unit = {},
    isSelectionMode: Boolean = false,
    selectedInvoices: Set<Long> = emptySet()
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(invoices, key = { it.id }) { invoice ->
                InvoiceItem(
                    invoice = invoice,
                    onClick = { onInvoiceClick(invoice.id) },
                    onDelete = { onDelete(invoice.id) },
                    onLongClick = { onLongClick(invoice.id) },
                    isSelected = selectedInvoices.contains(invoice.id),
                    isSelectionMode = isSelectionMode
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvoicesListScreenPreview() {
    ComposeTrainerTheme {
        val sampleProducts = listOf(
            ProductWithQuantity(
                product = Product(
                    id = 1,
                    name = "Smartphone",
                    price = 699L,
                    barcode = "123456789",
                    subCategoryId = 1,
                    date = System.currentTimeMillis(),
                    stock = 10,
                    image = null
                ),
                quantity = 2
            ),
            ProductWithQuantity(
                product = Product(
                    id = 2,
                    name = "Laptop",
                    price = 1299L,
                    barcode = "987654321",
                    subCategoryId = 1,
                    date = System.currentTimeMillis(),
                    stock = 5,
                    image = null
                ),
                quantity = 1
            )
        )

        val sampleInvoices = listOf(
            Invoice(
                id = 1,
                prefix = "INV",
                invoiceDate = "1403-02-16",
                invoiceNumber = 10001,
                products = sampleProducts.take(1),
                totalPrice = 1398L
            ),
            Invoice(
                id = 2,
                prefix = "INV",
                invoiceDate = "1403-02-17",
                invoiceNumber = 10002,
                products = sampleProducts,
                totalPrice = 2697L
            ),
            Invoice(
                id = 3,
                prefix = "INV",
                invoiceDate = "1403-02-18",
                invoiceNumber = 10003,
                products = sampleProducts.take(1),
                totalPrice = 1398L
            )
        )

        InvoicesListScreenPreviewContent(sampleInvoices)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesListScreenPreviewContent(invoices: List<Invoice>) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(str(R.string.sale_invoices), fontFamily = BHoma) },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = "Sort invoices"
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Add, contentDescription = "New Invoice")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                InvoicesLazyList(
                    invoices = invoices,
                    onInvoiceClick = { },
                    onDelete = { }
                )
            }
        }
    }
}