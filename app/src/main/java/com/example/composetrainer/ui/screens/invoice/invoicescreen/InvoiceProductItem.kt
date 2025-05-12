package com.example.composetrainer.ui.screens.invoice.invoicescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.utils.dimen

@Composable
fun InvoiceProductItem(
    productWithQuantity: ProductWithQuantity,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen(R.dimen.space_1)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen(R.dimen.space_3), vertical = dimen(R.dimen.space_2)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productWithQuantity.product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier.padding(top = dimen(R.dimen.space_1))
                ) {
                    Text(
                        text = "Qty: ${productWithQuantity.quantity}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))

                    Text(
                        text = "Price: ${productWithQuantity.product.price ?: 0}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            val itemTotal =
                productWithQuantity.product.price?.times(productWithQuantity.quantity) ?: 0

            Text(
                text = "$itemTotal",
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove"
                )
            }
        }
    }
}

@Preview(
    name = "Invoice Product Item Preview",
    showBackground = true
)
@Composable
fun InvoiceProductItemPreview() {
    ComposeTrainerTheme {
        InvoiceProductItem(
            productWithQuantity = ProductWithQuantity(
                product = Product(
                    id = 1,
                    name = "Sample Product",
                    barcode = "123456789",
                    price = 25000,
                    image = null,
                    categoryID = null,
                    date = System.currentTimeMillis(),
                    stock = 10
                ),
                quantity = 3
            ),
            onRemove = { }
        )
    }
}