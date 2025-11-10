package com.example.composetrainer.ui.screens.invoice.invoicescreen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.InvoiceProduct
import com.example.composetrainer.domain.model.InvoiceType
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.ui.screens.component.CurrencyIcon
import com.example.composetrainer.ui.theme.BKoodak
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.color.customError
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.price.PriceValidator

@Composable
fun InvoiceProductItem(
    productWithQuantity: InvoiceProduct,
    product: Product,
    invoiceType: InvoiceType = InvoiceType.SALE,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimen(R.dimen.space_1)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen(R.dimen.space_2))
            ) {
                // Product name and remove button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name.value,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.delete_24px),
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.customError,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "قیمت واحد: ",
                        fontFamily = Beirut_Medium,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Text(
                        text = PriceValidator.formatPrice(product.price.amount.toString()),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = BKoodak,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "موجودی: ${product.stock.value}",
                        fontFamily = BKoodak,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (invoiceType == InvoiceType.SALE &&
                                productWithQuantity.quantity.value >= product.stock.value)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Quantity control and total row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Quantity control
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .padding(horizontal = 4.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                val newQuantity = productWithQuantity.quantity.value - 1
                                if (newQuantity > 0) {
                                    onQuantityChange(newQuantity)
                                }
                            },
                            modifier = Modifier.size(32.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                    alpha = 0.7f
                                )
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Remove,
                                contentDescription = "کاهش",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            val isNearingLimit = invoiceType == InvoiceType.SALE &&
                                    productWithQuantity.quantity.value >= product.stock.value

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${productWithQuantity.quantity.value}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = BKoodak,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = if (isNearingLimit)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }


                        FilledTonalIconButton(
                            onClick = {
                                val newQuantity = productWithQuantity.quantity.value + 1
                                val stock = product.stock.value
                                if (newQuantity <= stock || invoiceType == InvoiceType.PURCHASE) {
                                    onQuantityChange(newQuantity)
                                }
                            },
                            modifier = Modifier.size(32.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                    alpha = 0.7f
                                ),
                                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.38f
                                ),
                                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.12f
                                )
                            ),

                            enabled = invoiceType == InvoiceType.PURCHASE ||
                                    productWithQuantity.quantity.value < product.stock.value
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "افزایش",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Total price
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(dimen(R.dimen.size_sm))
                        ) {
                            val itemTotal = productWithQuantity.calculateTotalRevenue()
                            Text(
                                text = PriceValidator.formatPrice(itemTotal.amount.toString()),
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .padding(end = dimen(R.dimen.space_1)),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = BKoodak,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            CurrencyIcon(
                                contentDescription = "Rial",
                                modifier = Modifier
                                    .size(dimen(R.dimen.size_sm))
                            )
                        }

                    }
                }
            }
        }
    }
}