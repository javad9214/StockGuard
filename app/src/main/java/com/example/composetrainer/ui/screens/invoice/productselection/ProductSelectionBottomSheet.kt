package com.example.composetrainer.ui.screens.invoice.productselection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product

/**
 * A bottom sheet for selecting products for an invoice.
 *
 * @param products The list of available products
 * @param onAddToInvoice Callback when a product is added to the invoice with quantity
 * @param onDismiss Callback when the sheet is dismissed
 * @param sheetState Optional sheet state for controlling the bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSelectionBottomSheet(
    products: List<Product>,
    onAddToInvoice: (Product, Int) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val layoutDirection = LocalLayoutDirection.current
    var selectedProductId by rememberSaveable { mutableStateOf<Long?>(null) }
    var quantity by rememberSaveable { mutableIntStateOf(1) }
    val selectedProduct = selectedProductId?.let { id -> products.find { it.id.value == id } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // HEADER
            selectedProduct?.name?.value?.let {
                SheetHeader(
                    title = it,
                    onNavClick = {
                        selectedProductId = null
                    },
                    navIcon = Icons.Filled.ArrowBack
                )
            }

            Divider(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(12.dp))

            // CONTENT
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                if (selectedProduct == null) {
                    ProductSelectionContent(
                        products = products,
                        onProductSelected = {
                            selectedProductId = it.id.value
                            quantity = 1 // reset on new product
                        }
                    )
                } else {
                    ProductDetailContent(
                        product = selectedProduct,
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        onBack = { selectedProductId = null },
                        onAddToInvoice = {
                            onAddToInvoice(selectedProduct, quantity)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
