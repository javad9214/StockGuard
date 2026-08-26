package ir.yar.anbar.ui.screens.invoice.productselection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.Barcode
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductFactory
import ir.yar.anbar.domain.model.ProductId
import ir.yar.anbar.domain.model.ProductName
import ir.yar.anbar.ui.components.customnavbars.BottomSheetDragHandle
import ir.yar.anbar.ui.theme.BHoma
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import ir.yar.anbar.ui.viewmodels.InvoiceViewModel
import ir.yar.anbar.ui.viewmodels.ProductsViewModel
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.str

/**
 * Stateful entry point: wires the shared ViewModels and delegates all rendering
 * to [AddProductToInvoiceContent], which stays preview- and test-friendly.
 */
@Composable
fun AddProductToInvoice(
    onClose: () -> Unit,
    productsViewModel: ProductsViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel()
) {
    val products by productsViewModel.products.collectAsState()
    val isLoading by productsViewModel.isLoading.collectAsState()
    val searchQuery by productsViewModel.searchQuery.collectAsState()

    AddProductToInvoiceContent(
        searchQuery = searchQuery,
        onSearchQueryChange = productsViewModel::updateSearchQuery,
        products = products,
        isLoading = isLoading,
        onClose = onClose,
        onProductSelected = { product ->
            invoiceViewModel.addToCurrentInvoice(product, quantity = 1)
            onClose()
        }
    )
}

@Composable
fun AddProductToInvoiceContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    products: List<Product>,
    isLoading: Boolean,
    onClose: () -> Unit,
    onProductSelected: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = dimen(R.dimen.space_10))
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = dimen(R.dimen.radius_xl),
                topEnd = dimen(R.dimen.radius_xl)
            ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
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
                            .padding(dimen(R.dimen.space_4))
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = str(R.string.close)
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
                Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))


                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen(R.dimen.space_4)),
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
                            IconButton(onClick = { onSearchQueryChange("") }) {
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



                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(dimen(R.dimen.space_4))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (products.isEmpty()) {
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

                                items(products, key = { it.id.value }) { product ->
                                    ProductSelectionItem(
                                        product = product,
                                        onClick = { onProductSelected(product) },
                                        modifier = Modifier.padding(vertical = dimen(R.dimen.space_1))
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

@Preview(showBackground = true, heightDp = 720)
@Composable
private fun AddProductToInvoiceContentPreview() {
    ComposeTrainerTheme {
        AddProductToInvoiceContent(
            searchQuery = "",
            onSearchQueryChange = {},
            products = listOf(
                previewProduct(1, "شیر پرچرب ۱ لیتری", 35000, 12),
                previewProduct(2, "پنیر سفید ایرانی", 89000, 4),
                previewProduct(3, "ماست موسیر ۵۰۰ گرمی", 62000, 0)
            ),
            isLoading = false,
            onClose = {},
            onProductSelected = {}
        )
    }
}

private fun previewProduct(
    id: Long,
    name: String,
    price: Long,
    stock: Int
): Product = ProductFactory.createBasic(
    name = ProductName(name),
    barcode = null,
    price = price,
    costPrice = price / 2,
    initialStock = stock
).copy(id = ProductId(id))
