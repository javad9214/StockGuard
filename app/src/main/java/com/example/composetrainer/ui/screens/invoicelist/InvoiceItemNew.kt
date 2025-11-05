package com.example.composetrainer.ui.screens.invoicelist

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.model.InvoiceNumber
import com.example.composetrainer.domain.model.InvoiceType
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.ui.screens.component.CurrencyIcon
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.theme.BKoodak
import com.example.composetrainer.ui.theme.BNazanin
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.theme.costPrice
import com.example.composetrainer.ui.theme.costPriceBg
import com.example.composetrainer.ui.theme.salePrice
import com.example.composetrainer.ui.theme.salePriceBg
import com.example.composetrainer.utils.dateandtime.FarsiDateUtil
import com.example.composetrainer.utils.dateandtime.FarsiDateUtil.getFormattedPersianDate
import com.example.composetrainer.utils.dateandtime.TimeStampUtil
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.price.PriceValidator
import com.example.composetrainer.utils.str

@Composable
fun InvoiceItemNew(
    invoiceWithProducts: InvoiceWithProducts,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onLongClick: () -> Unit = {},
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
) {

    val invoice = invoiceWithProducts.invoice
    val invoiceType = invoice.invoiceType
    val formattedDate: String = getFormattedPersianDate(invoice.invoiceDate)

    ElevatedCard(
        shape = RoundedCornerShape(
            bottomStart = dimen(R.dimen.radius_xl),
            bottomEnd = dimen(R.dimen.radius_xl)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            // first top row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (invoiceType == InvoiceType.SALE)
                            MaterialTheme.colorScheme.salePriceBg
                        else MaterialTheme.colorScheme.costPriceBg
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                val invoiceNumber = invoice.invoiceNumber.value.toString()
                Text(
                    text = " # $invoiceNumber ${str(R.string.invoice_number)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BKoodak,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(dimen(R.dimen.space_2))
                )

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = if (invoiceType == InvoiceType.SALE) str(R.string.sale_invoice)
                        else str(R.string.purchase_invoice),
                        fontSize = dimenTextSize(R.dimen.text_size_lg),
                        textAlign = TextAlign.Center,
                        fontFamily = Beirut_Medium,
                        color = if (invoiceType == InvoiceType.SALE)
                            MaterialTheme.colorScheme.salePrice
                        else MaterialTheme.colorScheme.costPrice
                    )

                    Box(
                        modifier = Modifier
                            .padding(dimen(R.dimen.space_2))
                            .size(dimen(R.dimen.size_lg))
                            .clip(RoundedCornerShape(dimen(R.dimen.radius_md)))
                            .background(
                                if (invoiceType == InvoiceType.SALE)
                                    MaterialTheme.colorScheme.salePrice.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.costPrice.copy(alpha = 0.1f)
                            )
                            .padding(dimen(R.dimen.space_2))
                    ) {
                        Icon(
                            painter = if (invoiceType == InvoiceType.SALE) painterResource(R.drawable.shopping_cart) else painterResource(
                                R.drawable.bag_happy
                            ),
                            modifier = Modifier.size(dimen(R.dimen.size_xs)),
                            contentDescription = "shopping Cart Icon",
                            tint = if (invoiceType == InvoiceType.SALE)
                                MaterialTheme.colorScheme.salePrice
                            else MaterialTheme.colorScheme.costPrice
                        )
                    }


                }

            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimen(R.dimen.space_2))
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = dimen(R.dimen.space_4))
                ) {

                    Text(
                        text = TimeStampUtil.formatTime(invoice.invoiceDate),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = BKoodak,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimenTextSize(R.dimen.text_size_md)
                    )

                    Box(
                        modifier = Modifier
                            .padding(dimen(R.dimen.space_2))
                            .size(dimen(R.dimen.size_lg))
                            .clip(RoundedCornerShape(dimen(R.dimen.radius_md)))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                            .padding(dimen(R.dimen.space_2))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.clock),
                            contentDescription = str(R.string.date),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(dimen(R.dimen.size_sm))
                        )
                    }

                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = BKoodak,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimenTextSize(R.dimen.text_size_md)
                    )

                    Box(
                        modifier = Modifier
                            .padding(dimen(R.dimen.space_2))
                            .size(dimen(R.dimen.size_lg))
                            .clip(RoundedCornerShape(dimen(R.dimen.radius_md)))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                            .padding(dimen(R.dimen.space_2))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = str(R.string.date),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(dimen(R.dimen.size_sm))
                        )
                    }


                }

            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimen(R.dimen.space_2), horizontal = dimen(R.dimen.space_4))
            ) {

                Text(
                    text = "${invoiceWithProducts.totalProductsCount}  ${str(R.string.goods)}",
                    fontSize = dimenTextSize(R.dimen.text_size_sm),
                    textAlign = TextAlign.Center,
                    fontFamily = Beirut_Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )


                Text(
                    text = str(R.string.number_of_good),
                    fontSize = dimenTextSize(R.dimen.text_size_sm),
                    textAlign = TextAlign.Center,
                    fontFamily = Beirut_Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimen(R.dimen.space_2), horizontal = dimen(R.dimen.space_4))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CurrencyIcon(
                        contentDescription = "Rial",
                        modifier = Modifier
                            .size(dimen(R.dimen.size_sm))
                            .padding(end = dimen(R.dimen.space_1))
                    )
                    Text(
                        text = PriceValidator.formatPrice(invoice.totalAmount?.amount.toString()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }


                Text(
                    text = str(R.string.total_amount),
                    fontSize = dimenTextSize(R.dimen.text_size_sm),
                    textAlign = TextAlign.Center,
                    fontFamily = Beirut_Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimen(R.dimen.space_4),
                        vertical = dimen(R.dimen.space_4)
                    ),
                shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = str(R.string.show_details),
                        fontFamily = BHoma,
                        fontSize = dimenTextSize(R.dimen.text_size_sm),
                        modifier = Modifier.padding(
                            vertical = dimen(R.dimen.space_1), horizontal = dimen(R.dimen.space_2)
                        )
                    )

                    Icon(
                        modifier = Modifier.size(dimen(R.dimen.size_sm)),
                        painter = painterResource(R.drawable.eye),
                        contentDescription = "show details"
                    )
                }


            }

        }
    }

}

@Preview(showBackground = true, name = "Invoice Item New Preview")
@Composable
fun InvoiceItemNewPreview() {
    // Create a simple default invoice with a specific invoice number for preview
    val mockInvoiceWithProducts =
        InvoiceWithProducts.createDefault(invoiceNumber = InvoiceNumber(42))

    ComposeTrainerTheme {
        InvoiceItemNew(
            invoiceWithProducts = mockInvoiceWithProducts,
            onClick = {},
            onDelete = {},
            onLongClick = {},
            isSelected = false,
            isSelectionMode = false
        )
    }
}