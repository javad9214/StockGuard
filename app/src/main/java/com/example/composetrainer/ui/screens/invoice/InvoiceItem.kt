package com.example.composetrainer.ui.screens.invoice

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.utils.DateFormatter.formatDate

@Composable
fun InvoiceItem(invoice: Invoice) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Invoice #${invoice.numberId}")
            Text("Date: ${formatDate(invoice.dateTime)}")
            Text("Total: $${invoice.totalPrice}")
            invoice.products.forEach { product ->
                Text("${product.quantity}x ${product.name}")
            }
        }
    }
}