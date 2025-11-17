package com.example.composetrainer.ui.screens.invoicelist

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.InvoiceType
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.ui.theme.BComps
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.InvoiceListEvent
import com.example.composetrainer.ui.viewmodels.InvoiceListViewModel
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str
import android.app.AlertDialog as AndroidAlertDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesListScreen(
    invoiceListViewModel: InvoiceListViewModel = hiltViewModel(),
    onCreateNew: () -> Unit,
    onInvoiceClick: (Long) -> Unit
) {
    // Collect UI state using lifecycle-aware collector
    val uiState by invoiceListViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error messages as Snackbar (better UX than Toast)
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            invoiceListViewModel.onEvent(InvoiceListEvent.ClearError)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top bar with actions
                InvoiceListTopBar(
                    isSelectionMode = uiState.isSelectionMode,
                    selectedCount = uiState.selectedInvoices.size,
                    sortNewestFirst = uiState.sortNewestFirst,
                    selectedTypeFilter = uiState.selectedTypeFilter,
                    hasInvoices = uiState.filteredInvoices.isNotEmpty(),
                    onEvent = invoiceListViewModel::onEvent
                )

                // Content area
                Box(modifier = Modifier.weight(1f)) {
                    when {
                        uiState.isLoading && uiState.filteredInvoices.isEmpty() -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        uiState.filteredInvoices.isEmpty() -> {
                            EmptyInvoicesState(
                                hasFilter = uiState.selectedTypeFilter != null,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        else -> {
                            InvoicesLazyList(
                                invoiceWithProductsList = uiState.filteredInvoices,
                                isSelectionMode = uiState.isSelectionMode,
                                selectedInvoices = uiState.selectedInvoices,
                                onInvoiceClick = { invoiceId ->
                                    if (uiState.isSelectionMode) {
                                        invoiceListViewModel.onEvent(
                                            InvoiceListEvent.ToggleInvoiceSelection(invoiceId)
                                        )
                                    } else {
                                        onInvoiceClick(invoiceId)
                                    }
                                },
                                onDelete = { invoiceId ->
                                    invoiceListViewModel.onEvent(
                                        InvoiceListEvent.DeleteInvoice(invoiceId)
                                    )
                                },
                                onLongClick = { invoiceId ->
                                    if (!uiState.isSelectionMode) {
                                        invoiceListViewModel.onEvent(InvoiceListEvent.ToggleSelectionMode)
                                        invoiceListViewModel.onEvent(
                                            InvoiceListEvent.ToggleInvoiceSelection(invoiceId)
                                        )
                                    }
                                }
                            )

                            // Show loading indicator on top when refreshing
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
    if (uiState.showDeleteConfirmationDialog) {
        DeleteConfirmationDialog(
            selectedCount = uiState.selectedInvoices.size,
            onConfirm = {
                invoiceListViewModel.onEvent(InvoiceListEvent.DeleteSelectedInvoices)
            },
            onDismiss = {
                invoiceListViewModel.onEvent(InvoiceListEvent.DismissDeleteConfirmation)
            }
        )
    }
}

@Composable
private fun InvoiceListTopBar(
    isSelectionMode: Boolean,
    selectedCount: Int,
    sortNewestFirst: Boolean,
    selectedTypeFilter: InvoiceType?,
    hasInvoices: Boolean,
    onEvent: (InvoiceListEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelectionMode) {
            SelectionModeTopBar(
                selectedCount = selectedCount,
                onDelete = { onEvent(InvoiceListEvent.ShowDeleteConfirmation) },
                onCancel = { onEvent(InvoiceListEvent.ToggleSelectionMode) }
            )
        } else {
            NormalModeTopBar(
                sortNewestFirst = sortNewestFirst,
                selectedTypeFilter = selectedTypeFilter,
                hasInvoices = hasInvoices,
                onSort = { onEvent(InvoiceListEvent.ToggleSortOrder) },
                onFilterChange = { type -> onEvent(InvoiceListEvent.FilterByType(type)) },
                onDeleteMode = { onEvent(InvoiceListEvent.ToggleSelectionMode) }
            )
        }
    }
}

@Composable
private fun SelectionModeTopBar(
    selectedCount: Int,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Text(
        text = if (selectedCount == 0) str(R.string.select_invoices_to_delete)
        else str(R.string.selected_invoices_count).format(selectedCount),
        fontFamily = BComps,
        fontSize = dimenTextSize(R.dimen.text_size_lg)
    )

    Row {
        // Delete button
        IconButton(
            onClick = onDelete,
            enabled = selectedCount > 0
        ) {
            Icon(
                painter = painterResource(id = R.drawable.delete_24px),
                contentDescription = str(R.string.delete),
                tint = if (selectedCount == 0) Color.Gray
                else MaterialTheme.colorScheme.error
            )
        }

        // Cancel selection mode
        IconButton(onClick = onCancel) {
            Icon(
                Icons.Default.Close,
                contentDescription = str(R.string.cancel)
            )
        }
    }
}

@Composable
private fun NormalModeTopBar(
    sortNewestFirst: Boolean,
    selectedTypeFilter: InvoiceType?,
    hasInvoices: Boolean,
    onSort: () -> Unit,
    onFilterChange: (InvoiceType?) -> Unit,
    onDeleteMode: () -> Unit
) {
    Text(
        str(R.string.sale_invoices),
        fontFamily = Beirut_Medium,
        fontSize = dimenTextSize(R.dimen.text_size_xl)
    )

    Row {
        // Filter button
        InvoiceTypeFilterMenu(
            selectedType = selectedTypeFilter,
            onFilterChange = onFilterChange
        )

        // Delete mode button
        if (hasInvoices) {
            IconButton(onClick = onDeleteMode) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_24px),
                    contentDescription = str(R.string.delete)
                )
            }
        }

        // Sort button
        IconButton(onClick = onSort) {
            Icon(
                Icons.Default.Sort,
                contentDescription = if (sortNewestFirst)
                    "Sort oldest to newest" else "Sort newest to oldest"
            )
        }
    }
}

@Composable
private fun InvoiceTypeFilterMenu(
    selectedType: InvoiceType?,
    onFilterChange: (InvoiceType?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = "Filter by type",
                tint = if (selectedType != null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        "All Types",
                        color = if (selectedType == null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    onFilterChange(null)
                    expanded = false
                }
            )

            InvoiceType.entries.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            type.name,
                            color = if (selectedType == type)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onFilterChange(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyInvoicesState(
    hasFilter: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (hasFilter)
                "No invoices found for this filter"
            else
                "No invoices available",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (hasFilter) {
            Text(
                text = "Try changing the filter",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimen(R.dimen.space_2))
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    selectedCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AndroidAlertDialog.Builder(context)
            .setTitle(context.getString(R.string.delete_selected_invoices))
            .setMessage(
                context.getString(R.string.are_you_sure_to_delete_selected_invoices)
                    .format(selectedCount)
            )
            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
                onDismiss()
            }
            .setOnDismissListener {
                onDismiss()
            }
            .show()
    }
}

@Composable
private fun InvoicesLazyList(
    invoiceWithProductsList: List<InvoiceWithProducts>,
    isSelectionMode: Boolean,
    selectedInvoices: Set<Long>,
    onInvoiceClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onLongClick: (Long) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = invoiceWithProductsList,
                key = { it.invoiceId.value }
            ) { invoice ->
                InvoiceItem(
                    invoiceWithProducts = invoice,
                    onClick = { onInvoiceClick(invoice.invoiceId.value) },
                    onDelete = { onDelete(invoice.invoiceId.value) },
                    onLongClick = { onLongClick(invoice.invoiceId.value) },
                    isSelected = selectedInvoices.contains(invoice.invoiceId.value),
                    isSelectionMode = isSelectionMode
                )
            }
        }
    }
}