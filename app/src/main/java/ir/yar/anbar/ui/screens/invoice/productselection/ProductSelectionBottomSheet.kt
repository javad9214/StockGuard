package ir.yar.anbar.ui.screens.invoice.productselection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import ir.yar.anbar.domain.model.Product


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

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

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
