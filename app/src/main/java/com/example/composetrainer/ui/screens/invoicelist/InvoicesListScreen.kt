package com.example.composetrainer.ui.screens.invoicelist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.composetrainer.ui.theme.BLotus
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
                    .padding(start = dimen(R.dimen.space_4), end = dimen(R.dimen.space_2)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(str(R.string.sale_invoices),fontFamily = BComps,
                    fontSize = dimenTextSize(R.dimen.text_size_lg)
                )

                IconButton(onClick = { viewModel.toggleSortOrder() }) {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = if (sortNewestFirst) "Sort oldest to newest" else "Sort newest to oldest"
                    )
                }


            }

            Box {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                    errorMessage != null -> Toast.makeText(
                        context,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    invoices.isEmpty() -> TODO("EmptyInvoiceView")

                    else -> InvoicesLazyList(
                        invoices = invoices,
                        onInvoiceClick = onInvoiceClick,
                        onDelete = viewModel::deleteInvoice
                    )

                }
            }
        }


    }
}

@Composable
private fun InvoicesLazyList(
    invoices: List<Invoice>,
    onInvoiceClick: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(invoices, key = { it.id }) { invoice ->
                InvoiceItem(
                    invoice = invoice,
                    onClick = { onInvoiceClick(invoice.id) },
                    onDelete = { onDelete(invoice.id) }
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