package com.example.composetrainer.ui.screens.invoicelist

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.composetrainer.ui.theme.BKoodak
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.BNazanin
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.utils.PriceValidator.formatPrice
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
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

        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    str(R.string.invoice_details),
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_xl)
                )

                Row {

                    IconButton(onClick = { onEditInvoice(invoiceId) }) {
                        Icon(painter = painterResource(id = R.drawable.edit_24px), contentDescription = str(R.string.save))
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(painter = painterResource(id = R.drawable.delete_24px), contentDescription = str(R.string.delete))
                    }

                    IconButton(onClick = onNavigateBack) {
                        Icon(painter = painterResource(id = R.drawable.arrow_back_ios_new_24px), contentDescription = str(R.string.back))
                    }
                }

            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                    invoice == null -> {
                        Text(
                            str(R.string.product_not_found),
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(str(R.string.delete))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
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
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoRow(
                        label = str(R.string.invoice_number),
                        value = "${invoice.prefix}-${invoice.invoiceNumber}"
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    InfoRow(
                        label = str(R.string.date),
                        value = invoice.invoiceDate
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    InfoRow(
                        label = str(R.string.total),
                        value = formatPrice(invoice.totalPrice.toString()),
                        isAmount = true
                    )
                }
            }
        }

        // Products section header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    str(R.string.products),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BHoma,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(R.string.product_count, invoice.products.size),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = BMitra,
                )
            }

            // Column headers
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        str(R.string.products),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(2f)
                    )

                    Text(
                        str(R.string.product_unit_price),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        str(R.string.quantity_short),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(0.7f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        str(R.string.product_total),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1.3f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Product items
        items(invoice.products) { productWithQty ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(
                            productWithQty.product.name ?: str(R.string.product_not_found),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = BNazanin,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        formatPrice((productWithQty.product.price ?: 0).toString()),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        "${productWithQty.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = BMitra,
                        modifier = Modifier
                            .weight(0.6f)
                            .padding(horizontal = 4.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )



                    Text(
                        formatPrice(
                            ((productWithQty.product.price
                                ?: 0) * productWithQty.quantity).toString()
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1.1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.toman),
                        contentDescription = "toman",
                        modifier = Modifier
                            .size(dimen(R.dimen.size_sm))
                            .weight(0.3f)
                            .padding(start = dimen(R.dimen.space_1))
                    )


                }
            }
        }

        // Total section at the bottom
        item {
            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        str(R.string.invoice_total_amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = BNazanin,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Row {
                        Text(
                            formatPrice(invoice.totalPrice.toString()),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.toman),
                            contentDescription = "toman",
                            modifier = Modifier
                                .size(dimen(R.dimen.size_sm))
                                .padding(start = dimen(R.dimen.space_1))
                        )
                    }


                }
            }

            // Add some space at the bottom
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    isAmount: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            fontFamily = BMitra,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (isAmount) {
            Row {
                Text(
                    formatPrice(value),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    painter = painterResource(id = R.drawable.toman),
                    contentDescription = "toman",
                    modifier = Modifier
                        .size(dimen(R.dimen.size_sm))
                        .padding(start = dimen(R.dimen.space_1))
                )
            }
        } else {
            Text(
                formatPrice(value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = BMitra,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
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
                date = System.currentTimeMillis(),
                costPrice = null,
                description = null,
                supplierId = null,
                unit = null,
                minStockLevel = null,
                maxStockLevel = null,
                isActive = true,
                tags = null,
                lastSoldDate = null
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
                date = System.currentTimeMillis(),
                costPrice = null,
                description = null,
                supplierId = null,
                unit = null,
                minStockLevel = null,
                maxStockLevel = null,
                isActive = true,
                tags = null,
                lastSoldDate = null
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
                date = System.currentTimeMillis(),
                costPrice = null,
                description = null,
                supplierId = null,
                unit = null,
                minStockLevel = null,
                maxStockLevel = null,
                isActive = true,
                tags = null,
                lastSoldDate = null
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
                date = System.currentTimeMillis(),
                costPrice = null,
                description = null,
                supplierId = null,
                unit = null,
                minStockLevel = null,
                maxStockLevel = null,
                isActive = true,
                tags = null,
                lastSoldDate = null
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
                date = System.currentTimeMillis(),
                costPrice = null,
                description = null,
                supplierId = null,
                unit = null,
                minStockLevel = null,
                maxStockLevel = null,
                isActive = true,
                tags = null,
                lastSoldDate = null
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
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        navigationIcon = {
                            IconButton(onClick = { }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = str(R.string.back)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(Icons.Default.Edit, contentDescription = str(R.string.save))
                            }
                            IconButton(onClick = { }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = str(R.string.delete)
                                )
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
