package com.example.composetrainer.ui.screens.invoice.productselection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.ui.components.BottomSheetDragHandle
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.viewmodels.InvoiceViewModel
import com.example.composetrainer.ui.viewmodels.ProductsViewModel
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.str

@Composable
fun AddProductToInvoice(
    onClose: () -> Unit = {},
    productsViewModel: ProductsViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
) {
    val products by productsViewModel.products.collectAsState()
    val filteredProducts by productsViewModel.filteredProducts.collectAsState()
    val isLoading by productsViewModel.isLoading.collectAsState()
    val searchQuery by productsViewModel.searchQuery.collectAsState()

    fun onProductSelected(product: Product) {
        invoiceViewModel.addToCurrentInvoice(product, 1)
        onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimen(R.dimen.space_10))
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = dimen(R.dimen.radius_xl),
                topEnd = dimen(R.dimen.radius_xl)
            ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp,
            )
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                BottomSheetDragHandle(
                    onDragDown = onClose
                )

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                        Text(
                            text = str(R.string.add_to_invoice),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.End,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))


                TextField(
                    value = searchQuery,
                    onValueChange = { productsViewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text(
                            text = str(R.string.search_products),
                            fontFamily = BHoma
                        )
                    },
                    textStyle = TextStyle(fontFamily = BHoma),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { productsViewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = str(R.string.clear_search)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = MaterialTheme.shapes.medium
                )



                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (filteredProducts.isEmpty()) {
                        Text(
                            text = str(R.string.no_products_available),
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            items(filteredProducts, key = { it.id }) { product ->
                                ProductSelectionItem(
                                    product = product,
                                    onClick = { onProductSelected(product) },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                            }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddProductToInvoicePreview() {
    MaterialTheme {
        // In a real scenario, the ViewModel would be provided by Hilt
        // This is just a preview, so we're not showing actual data
        AddProductToInvoice()
    }
}