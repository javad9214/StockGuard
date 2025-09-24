package com.example.composetrainer.ui.screens.productlist.serverlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.price.PriceValidator
import com.example.composetrainer.utils.str

@Composable
fun ServerProductItem(
    product: Product,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onProductClick: () -> Unit = {}
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var showMenu by remember { mutableStateOf(false) }
    var itemClicked by remember { mutableStateOf(false) }

    val myFontFamily = FontFamily(
        Font(R.font.b_koodak_bd, FontWeight.Normal)
    )
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clickable { onProductClick() },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        text = product.name.value,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = myFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Category ID
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.subcategoryId?.value.toString(),
                            fontSize = dimenTextSize(R.dimen.text_size_md),
                            fontFamily = BHoma
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Category",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Barcode
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        product.barcode?.value?.let {
                            Text(
                                text = it,
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                fontFamily = BHoma
                            )
                        } ?: Text(
                            text = "N/A",
                            fontSize = dimenTextSize(R.dimen.text_size_md),
                            fontFamily = BHoma
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.barcode_24px),
                            contentDescription = "barcode"
                        )


                    }
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )


                // Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {



                    Spacer(modifier = Modifier.width(8.dp))


                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(dimen(R.dimen.space_2))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {
                            itemClicked = true
                            onAdd()
                        },
                        shape = RoundedCornerShape(dimen(R.dimen.radius_sm)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (itemClicked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        if (itemClicked) {
                            // After click: show text + icon
                            Text(
                                text = str(R.string.product_added),
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                fontFamily = BMitra
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(R.drawable.check_24px),
                                contentDescription = null
                            )
                        } else {
                            // Before click: normal text
                            Text(
                                text = str(R.string.add_to_my_products),
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                fontFamily = BMitra
                            )
                        }
                    }

                    Column {
                        Text(
                            textAlign = TextAlign.Right,
                            text = stringResource(id = R.string.suggestion_price),
                            fontSize = dimenTextSize(R.dimen.text_size_xs),
                            fontFamily = BMitra
                        )

                        Row {
                            Icon(
                                painter = painterResource(id = R.drawable.toman),
                                contentDescription = "Date",
                                modifier = Modifier
                                    .size(dimen(R.dimen.size_sm))
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                textAlign = TextAlign.Start,
                                text = PriceValidator.formatPrice(product.price.amount.toString()),
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                fontFamily = BHoma
                            )
                        }
                    }


                }


            }
        }
    }
}