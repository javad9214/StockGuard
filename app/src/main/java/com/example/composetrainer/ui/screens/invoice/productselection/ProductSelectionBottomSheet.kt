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
    val selectedProduct = selectedProductId?.let { id -> products.find { it.id == id } }

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
            SheetHeader(
                title = selectedProduct?.name ?: stringResource(R.string.select_product),
                onNavClick = {
                    if (selectedProduct == null) onDismiss() else selectedProductId = null
                },
                navIcon = if (selectedProduct == null) Icons.Filled.Close else Icons.Filled.ArrowBack
            )

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
                            selectedProductId = it.id
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



@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductSelectionBottomSheetPreview() {
    val sampleProducts = listOf(
        Product(
            id = 1L,
            name = "گوشی هوشمند",
            barcode = "123456789",
            price = 69999L,
            image = null,
            subCategoryId = 1,
            date = System.currentTimeMillis(),
            stock = 15,
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
        Product(
            id = 2L,
            name = "هدفون بی‌سیم",
            barcode = "987654321",
            price = 14999L,
            image = null,
            subCategoryId = 2,
            date = System.currentTimeMillis(),
            stock = 8,
            costPrice = null,
            description = null,
            supplierId = null,
            unit = null,
            minStockLevel = null,
            maxStockLevel = null,
            isActive = true,
            tags = null,
            lastSoldDate = null
        )
    )

    Surface {
        ProductSelectionBottomSheet(
            products = sampleProducts,
            onAddToInvoice = { _, _ -> },
            onDismiss = {}
        )
    }
}