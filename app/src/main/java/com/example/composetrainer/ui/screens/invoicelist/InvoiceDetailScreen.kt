package com.example.composetrainer.ui.screens.invoicelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.calculateTotalAmount
import com.example.composetrainer.domain.model.calculateTotalCost
import com.example.composetrainer.ui.screens.component.CurrencyIcon
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.BNazanin
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.InvoiceListViewModel
import com.example.composetrainer.utils.dateandtime.FarsiDateUtil.getFormattedPersianDate
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.price.PriceValidator.formatPrice
import com.example.composetrainer.utils.str



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoiceId: Long,
    onNavigateBack: () -> Unit,
    onEditInvoice: (Long) -> Unit,
    viewModel: InvoiceListViewModel = hiltViewModel()
) {
    // Collect UI state using lifecycle-aware collector
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Find the specific invoice from the filtered list
    val invoice = remember(uiState.filteredInvoices, invoiceId) {
        uiState.filteredInvoices.find { it.invoice.id.value == invoiceId }
    }

    // Show error messages as Snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(InvoiceListEvent.ClearError)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                InvoiceDetailTopBar(
                    onNavigateBack = onNavigateBack,
                    onEdit = { onEditInvoice(invoiceId) },
                    onDelete = { showDeleteConfirmDialog = true }
                )

                // Content area
                Box(modifier = Modifier.weight(1f)) {
                    when {
                        uiState.isLoading && invoice == null -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        invoice == null -> {
                            InvoiceNotFoundState(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        else -> {
                            InvoiceDetailContent(
                                invoiceWithProducts = invoice,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay loading indicator when updating
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = dimen(R.dimen.space_4))
                                )
                            }
                        }
                    }
                }
            }

            // Snackbar for errors
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(dimen(R.dimen.space_4))
            )
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        InvoiceDeleteConfirmationDialog(
            onConfirm = {
                viewModel.onEvent(InvoiceListEvent.DeleteInvoice(invoiceId))
                showDeleteConfirmDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }
}

@Composable
private fun InvoiceDetailTopBar(
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
            IconButton(onClick = onEdit) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_24px),
                    contentDescription = str(R.string.save)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_24px),
                    contentDescription = str(R.string.delete)
                )
            }

            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back_ios_new_24px),
                    contentDescription = str(R.string.back)
                )
            }
        }
    }
}

@Composable
private fun InvoiceNotFoundState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = str(R.string.product_not_found),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InvoiceDeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(str(R.string.Delete_invoice)) },
        text = { Text(str(R.string.are_you_sure_to_delete_this_invoice)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(str(R.string.delete))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(str(R.string.cancel))
            }
        }
    )
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
            InvoiceHeaderCard(invoiceWithProducts = invoiceWithProducts)
        }

        // Products section header
        item {
            ProductsSectionHeader(
                productsCount = invoiceWithProducts.products.size
            )
        }

        // Product items
        items(
            items = invoiceWithProducts.products,
            key = { it.id.value }
        ) { product ->
            val invoiceProduct = invoiceWithProducts.invoiceProducts.find {
                it.productId == product.id
            }

            invoiceProduct?.let {
                ProductItemCard(
                    product = product,
                    invoiceProduct = it
                )
            }
        }

        // Total section at the bottom
        item {
            InvoiceTotalSection(invoiceWithProducts = invoiceWithProducts)
        }
    }
}

@Composable
private fun InvoiceHeaderCard(
    invoiceWithProducts: InvoiceWithProducts
) {
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
                value = "${invoiceWithProducts.invoice.prefix.value}-${invoiceWithProducts.invoiceNumber.value}"
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            InfoRow(
                label = str(R.string.date),
                value = getFormattedPersianDate(invoiceWithProducts.invoice.invoiceDate)
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            InfoRow(
                label = str(R.string.total),
                value = formatPrice(invoiceWithProducts.calculateTotalAmount().amount.toString()),
                isAmount = true
            )
        }
    }
}

@Composable
private fun ProductsSectionHeader(
    productsCount: Int
) {
    Column {
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
                text = stringResource(R.string.product_count, productsCount),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = BMitra
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
}

@Composable
private fun ProductItemCard(
    product: com.example.composetrainer.domain.model.Product,
    invoiceProduct: com.example.composetrainer.domain.model.InvoiceProduct
) {
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
            Column(modifier = Modifier.weight(2f)) {
                Text(
                    product.name.value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = BNazanin,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                formatPrice(product.price.amount.toString()),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = invoiceProduct.quantity.value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = BMitra,
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 4.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = formatPrice(invoiceProduct.calculateTotalRevenue().amount.toString()),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1.1f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )

            CurrencyIcon(
                contentDescription = "Rial",
                modifier = Modifier
                    .size(dimen(R.dimen.size_sm))
                    .weight(0.3f)
                    .padding(start = dimen(R.dimen.space_1))
            )
        }
    }
}

@Composable
private fun InvoiceTotalSection(
    invoiceWithProducts: InvoiceWithProducts
) {
    Column {
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
                        formatPrice(invoiceWithProducts.calculateTotalCost().amount.toString()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    CurrencyIcon(
                        contentDescription = "Currency Icon",
                        modifier = Modifier
                            .size(dimen(R.dimen.size_sm))
                            .padding(start = dimen(R.dimen.space_1))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
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

                CurrencyIcon(
                    contentDescription = "Currency Icon",
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

