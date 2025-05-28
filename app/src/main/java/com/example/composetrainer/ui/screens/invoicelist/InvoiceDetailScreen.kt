package com.example.composetrainer.ui.screens.invoicelist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.utils.PriceValidator.formatPrice
import com.example.composetrainer.utils.str


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoiceId: Long,
    onNavigateBack: () -> Unit,
    onEditInvoice: (Long) -> Unit,
    viewModel: InvoiceViewModel = hiltViewModel()
) {
    val invoices by viewModel.invoices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    val invoice = invoices.find { it.id == invoiceId }

    // Dialog state
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Show error message if any
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            // You could add Toast implementation here
        }
    }

    // Ensure we have loaded invoices
    LaunchedEffect(Unit) {
        viewModel.loadInvoices()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(str(R.string.invoice_details), fontFamily = BHoma) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEditInvoice(invoiceId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit invoice")
                        }
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete invoice")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                    invoice == null -> {
                        Text(
                            "Invoice not found",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        InvoiceDetailContent(
                            invoice = invoice,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {

        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(str(R.string.Delete_invoice)) },
            text = { Text(str(R.string.are_you_sure_to_delete_this_invoice)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteInvoice(invoiceId)
                        showDeleteConfirmDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text(str(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(str(R.string.cancel))
                }
            }
        )

    }
}

@Composable
private fun InvoiceDetailContent(
    invoice: Invoice,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Invoice header information
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            str(R.string.invoice_number),
                            fontWeight = FontWeight.Bold
                        )
                        Text("${invoice.prefix}-${invoice.invoiceNumber}")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            str(R.string.date),
                            fontWeight = FontWeight.Bold
                        )
                        Text(invoice.invoiceDate)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            str(R.string.total),
                            fontWeight = FontWeight.Bold
                        )
                        Text(formatPrice(invoice.totalPrice.toString()))
                    }
                }
            }
        }

        // Products section header
        item {
            Text(
                str(R.string.products),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = BHoma
            )
        }

        // Product items
        items(invoice.products) { productWithQty ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            productWithQty.product.name ?: "Unknown product",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Show product price or 0 if null, formatted as currency
                        Text(
                            text = formatPrice((productWithQty.product.price ?: 0).toString()),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Qty: ${productWithQty.quantity}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Total: ${formatPrice(((productWithQty.product.price ?: 0) * productWithQty.quantity).toString())}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Total section at the bottom
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total Invoice Amount:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        formatPrice(invoice.totalPrice.toString()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvoiceDetailScreenPreview() {
    // Sample invoice data for preview
    val sampleProducts = listOf(
        ProductWithQuantity(
            product = Product(
                id = 1L,
                name = "Sample Product 1",
                price = 50000L,
                stock = 10,
                barcode = "123456789",
                image = null,
                subCategoryId = 1,
                date = System.currentTimeMillis()
            ),
            quantity = 2
        ),
        ProductWithQuantity(
            product = Product(
                id = 2L,
                name = "Sample Product 2",
                price = 75000L,
                stock = 5,
                barcode = "223456789",
                image = null,
                subCategoryId = 2,
                date = System.currentTimeMillis()
            ),
            quantity = 1
        ),
        ProductWithQuantity(
            product = Product(
                id = 3L,
                name = "Sample Product 3",
                price = 120000L,
                stock = 3,
                barcode = "323456789",
                image = null,
                subCategoryId = 1,
                date = System.currentTimeMillis()
            ),
            quantity = 3
        )
    )

    val sampleInvoice = Invoice(
        id = 1L,
        prefix = "INV",
        invoiceNumber = 1001L,
        invoiceDate = "1402/12/15",
        products = sampleProducts,
        totalPrice = 410000L
    )

    InvoiceDetailContent(invoice = sampleInvoice)
}

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreenFullPreview() {
    val sampleProducts = listOf(
        ProductWithQuantity(
            product = Product(
                id = 1L,
                name = "Sample Product 1",
                price = 50000L,
                stock = 10,
                barcode = "123456789",
                image = null,
                subCategoryId = 1,
                date = System.currentTimeMillis()
            ),
            quantity = 2
        ),
        ProductWithQuantity(
            product = Product(
                id = 2L,
                name = "Sample Product 2",
                price = 75000L,
                stock = 5,
                barcode = "223456789",
                image = null,
                subCategoryId = 2,
                date = System.currentTimeMillis()
            ),
            quantity = 1
        )
    )

    val sampleInvoice = Invoice(
        id = 1L,
        prefix = "INV",
        invoiceNumber = 1001L,
        invoiceDate = "1402/12/15",
        products = sampleProducts,
        totalPrice = 175000L
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val invoices = remember { mutableStateOf(listOf(sampleInvoice)) }
        val isLoading = remember { mutableStateOf(false) }
        val errorMessage = remember { mutableStateOf<String?>(null) }

        MaterialTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(str(R.string.invoice_details), fontFamily = BHoma) },
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit invoice")
                            }
                            IconButton(onClick = { }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete invoice")
                            }

                            IconButton(onClick = { }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    InvoiceDetailContent(
                        invoice = sampleInvoice,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}