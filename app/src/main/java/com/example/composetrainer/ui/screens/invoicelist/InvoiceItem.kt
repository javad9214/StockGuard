package com.example.composetrainer.ui.screens.invoicelist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.ui.theme.BNazanin
import com.example.composetrainer.ui.theme.BRoya
import com.example.composetrainer.utils.PriceValidator
import com.example.composetrainer.utils.dateandtime.FarsiDateUtil.getFormattedPersianDate
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InvoiceItem(
    invoiceWithProducts: InvoiceWithProducts,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onLongClick: () -> Unit = {},
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
) {
    val invoice = invoiceWithProducts.invoice
    val formattedDate: String = getFormattedPersianDate(invoice.invoiceDate)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSelectionMode && isSelected)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
            ) {
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onClick() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(8.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(start = if (isSelectionMode) 40.dp else 0.dp),
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
                                contentDescription = str(R.string.date),
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
                                fontWeight = FontWeight.Bold,
                                fontSize = dimenTextSize(R.dimen.text_size_md)
                            )
                        }
                        val invoiceNumber = invoice.invoiceNumber.value.toString()
                        Text(
                            text = "#$invoiceNumber :  ${str(R.string.invoice_number)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BNazanin,
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
                                contentDescription = str(R.string.toman),
                                modifier = Modifier
                                    .size(dimen(R.dimen.size_md))
                                    .padding(end = 4.dp)
                            )

                            Text(
                                text = PriceValidator.formatPrice(invoice.totalAmount?.amount.toString()),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${str(R.string.items)} : ${invoiceWithProducts.totalProductsCount}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = BRoya,
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = str(R.string.items),
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
    }
}