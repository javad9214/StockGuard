package com.example.composetrainer.ui.screens.invoicelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.ui.theme.BNazanin
import com.example.composetrainer.ui.theme.BRoya
import com.example.composetrainer.utils.PriceValidator
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun InvoiceItem(
    invoice: Invoice,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    formattedDate: String = invoice.invoiceDate
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_today_24px),
                        contentDescription = "Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(dimen(R.dimen.size_sm))
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = BNazanin,
                        fontSize = dimenTextSize(R.dimen.text_size_md)
                    )
                }

                Text(
                    text = "#${invoice.invoiceNumber} :  ${str(R.string.invoice_number)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.toman),
                        contentDescription = "toman",
                        modifier = Modifier
                            .size(dimen(R.dimen.size_md))
                            .padding(end = 4.dp)
                    )

                    Text(
                        text = PriceValidator.formatPrice(invoice.totalPrice.toString()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }


                Row(verticalAlignment = Alignment.Bottom) {

                    Text(
                        text = "${str(R.string.items)} : ${invoice.products.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = BRoya,
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Items",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .size(dimen(R.dimen.size_sm))
                            .padding(start = 4.dp, end = 4.dp)
                    )

                }
            }
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