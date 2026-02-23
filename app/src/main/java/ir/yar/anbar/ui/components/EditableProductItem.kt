package ir.yar.anbar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.yar.anbar.domain.model.InvoiceProduct
import ir.yar.anbar.domain.model.Product

@Composable
fun EditableProductItem(
    item: InvoiceProduct,
    productItem: Product,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.padding(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(productItem.name.value, style = MaterialTheme.typography.titleMedium)
                Text("Qty: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = item.calculateTotalRevenue().amount.toString(),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, "Remove")
            }
        }
    }
}