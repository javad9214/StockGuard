package com.example.composetrainer.ui.screens.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.ui.theme.ComposeTrainerTheme

@Composable
fun ProductItem(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: $${product.price ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Barcode: ${product.barcode ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProductItem() {
    ComposeTrainerTheme {
        ProductItem(
            product = Product(
                id = 1,
                name = "Sample Product",
                barcode = "123456789",
                price = 1000,
                image = null,
                categoryID = 1
            )
        )
    }
}