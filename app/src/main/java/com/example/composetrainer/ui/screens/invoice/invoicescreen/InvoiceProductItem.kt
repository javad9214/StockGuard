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
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.ui.theme.BNazanin
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.utils.PriceValidator
import com.example.composetrainer.utils.dimen

@Composable
fun InvoiceProductItem(
    productWithQuantity: ProductWithQuantity,
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
                        text = productWithQuantity.product.name,
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
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.error,
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
                        fontFamily = BNazanin,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    
                    Text(
                        text = "${productWithQuantity.product.price ?: 0}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Available stock indicator
                    Text(
                        text = "موجودی: ${productWithQuantity.product.stock ?: 0}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if ((productWithQuantity.quantity) >= (productWithQuantity.product.stock
                                    ?: 0)
                            )
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
                                val newQuantity = productWithQuantity.quantity - 1
                                if (newQuantity > 0) {
                                    onQuantityChange(newQuantity)
                                }
                            },
                            modifier = Modifier.size(32.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
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
                            val isNearingLimit =
                                productWithQuantity.quantity >= (productWithQuantity.product.stock
                                    ?: 0)

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${productWithQuantity.quantity}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = if (isNearingLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                        
                        FilledTonalIconButton(
                            onClick = {
                                val newQuantity = productWithQuantity.quantity + 1
                                val stock = productWithQuantity.product.stock ?: 0
                                if (newQuantity <= stock) {
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
                            enabled = productWithQuantity.quantity < (productWithQuantity.product.stock
                                ?: 0)
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

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(dimen(R.dimen.size_sm))
                        ) {
                            val itemTotal = productWithQuantity.product.price?.times(productWithQuantity.quantity) ?: 0
                            Text(
                                text = PriceValidator.formatPrice(itemTotal.toString()),
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .padding(end = dimen(R.dimen.space_1)),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.toman),
                                contentDescription = "Date",
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

@Preview(
    name = "Invoice Product Item",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
fun InvoiceProductItemPreview() {
    ComposeTrainerTheme {
        InvoiceProductItem(
            productWithQuantity = ProductWithQuantity(
                product = Product(
                    id = 1,
                    name = "محصول نمونه",
                    barcode = "123456789",
                    price = 25000,
                    image = null,
                    categoryID = null,
                    date = System.currentTimeMillis(),
                    stock = 10
                ),
                quantity = 3
            ),
            onRemove = { },
            onQuantityChange = { }
        )
    }
}