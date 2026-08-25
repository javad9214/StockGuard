package ir.yar.anbar.ui.screens.invoice.invoicescreen


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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
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
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.InvoiceProduct
import ir.yar.anbar.domain.model.InvoiceType
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.ui.screens.component.CurrencyIcon
import ir.yar.anbar.ui.theme.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.theme.color.customError
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.price.PriceValidator
import ir.yar.anbar.utils.str

@Composable
fun InvoiceProductItem(
    productWithQuantity: InvoiceProduct,
    product: Product,
    invoiceType: InvoiceType = InvoiceType.SALE,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    val isSale = invoiceType == InvoiceType.SALE
    val isAtStockLimit = isSale && productWithQuantity.quantity.value >= product.stock.value

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimen(R.dimen.space_1)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            shape = RoundedCornerShape(dimen(R.dimen.radius_md))
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
                            fontSize = dimenTextSize(R.dimen.text_size_md)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .size(dimen(R.dimen.size_md))
                            .clip(CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.delete_24px),
                            contentDescription = str(R.string.delete),
                            tint = MaterialTheme.colorScheme.customError,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                // Price information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${str(R.string.product_unit_price)}: ",
                        fontFamily = Beirut_Medium,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    val priceToShow = if (isSale) {
                        product.price.amount
                    } else {
                        product.costPrice.amount
                    }

                    Text(
                        text = PriceValidator.formatPrice(priceToShow.toString()),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = BKoodak,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )


                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = str(R.string.stock_with_value, product.stock.value),
                        fontFamily = BKoodak,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isAtStockLimit)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))

                // Quantity control and total row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Quantity control
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(dimen(R.dimen.radius_xs)))
                            .padding(horizontal = dimen(R.dimen.space_1))
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                val newQuantity = productWithQuantity.quantity.value - 1
                                if (newQuantity > 0) {
                                    onQuantityChange(newQuantity)
                                }
                            },
                            modifier = Modifier.size(dimen(R.dimen.size_md)),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                    alpha = 0.7f
                                )
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Remove,
                                contentDescription = str(R.string.decrease),
                                modifier = Modifier.size(dimen(R.dimen.size_xs))
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .padding(horizontal = dimen(R.dimen.space_1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = productWithQuantity.quantity.value.toString(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = BKoodak,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = if (isAtStockLimit)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }


                        FilledTonalIconButton(
                            onClick = {
                                val newQuantity = productWithQuantity.quantity.value + 1
                                if (!isSale || newQuantity <= product.stock.value) {
                                    onQuantityChange(newQuantity)
                                }
                            },
                            modifier = Modifier.size(dimen(R.dimen.size_md)),
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

                            enabled = !isSale || productWithQuantity.quantity.value < product.stock.value
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = str(R.string.increase),
                                modifier = Modifier.size(dimen(R.dimen.size_xs))
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
                            val itemTotal = if (isSale) {
                                productWithQuantity.calculateTotalRevenue()
                            } else {
                                productWithQuantity.calculateTotalCost()
                            }

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
