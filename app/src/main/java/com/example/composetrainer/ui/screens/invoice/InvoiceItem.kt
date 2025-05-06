package com.example.composetrainer.ui.screens.invoice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.composetrainer.domain.model.Invoice

@Composable
fun InvoiceItem(
    invoice: Invoice,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Invoice #${invoice.invoiceNumber}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = invoice.invoiceDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total: $${invoice.totalPrice}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Items: ${invoice.products.size}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvoiceItemPreview() {
    InvoiceItem(
        Invoice(
            id = 1,
            invoiceNumber = 12345,
            invoiceDate = "1403-02-16",
            prefix = "INV",
            totalPrice = 1000,
            products = emptyList()
        ),
        onClick = {},
        onDelete = {}
    )
}