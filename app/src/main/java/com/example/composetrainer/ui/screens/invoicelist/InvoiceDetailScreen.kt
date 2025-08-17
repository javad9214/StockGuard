package com.example.composetrainer.ui.screens.invoicelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.calculateTotalAmount
import com.example.composetrainer.domain.model.calculateTotalCost
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.BNazanin
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.InvoiceListViewModel
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
    viewModel: InvoiceListViewModel = hiltViewModel()
) {
    val invoiceWithProducts by viewModel.invoices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    val invoice = invoiceWithProducts.find { it.invoice.id.value == invoiceId }

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
                            invoiceWithProducts = invoice,
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
    invoiceWithProducts: InvoiceWithProducts,
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
                        value = "${invoiceWithProducts.invoice.prefix}-${invoiceWithProducts.invoiceNumber}"
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    InfoRow(
                        label = str(R.string.date),
                        value = invoiceWithProducts.invoice.invoiceDate.toString()
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    InfoRow(
                        label = str(R.string.total),
                        value = formatPrice(invoiceWithProducts.calculateTotalAmount().toString()),
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
                    text = stringResource(R.string.product_count, ),
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
        items(invoiceWithProducts.products.size) { item  ->
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
                            item.product.name ?: str(R.string.product_not_found),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = BNazanin,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        formatPrice((item.product.price ?: 0).toString()),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        "${item.quantity}",
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
                            ((item.product.price
                                ?: 0) * item.quantity).toString()
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
                            formatPrice(invoiceWithProducts.calculateTotalCost().toString()),
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

