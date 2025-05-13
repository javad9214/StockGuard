package com.example.composetrainer.ui.screens.product

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.utils.PriceValidator
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProductItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onIncreaseStock: () -> Unit,
    onDecreaseStock: () -> Unit
) {

    var showMenu by remember { mutableStateOf(false) }

    val myFontFamily = FontFamily(
        Font(R.font.b_koodak_bd, FontWeight.Normal) // Link to res/font
    )

    Card(
        modifier = Modifier.fillMaxWidth()
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
                    text = product.name,
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
                        text = "${product.categoryID ?: "N/A"}",
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
                    Text(
                        text = product.barcode ?: "N/A",
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

            // Stock Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Stock: ${product.stock}",
                    fontSize = dimenTextSize(R.dimen.text_size_md),
                    fontFamily = BHoma
                )
                Row {
                    IconButton(onClick = onDecreaseStock) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease Stock")
                    }
                    IconButton(onClick = onIncreaseStock) {
                        Icon(Icons.Default.Add, contentDescription = "Increase Stock")
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Price

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.toman),
                    contentDescription = "Date",
                    modifier = Modifier
                        .size(dimen(R.dimen.size_sm))
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    modifier = Modifier.weight(4f),
                    textAlign = TextAlign.Start,
                    text = PriceValidator.formatPrice(product.price.toString()),
                    fontSize = dimenTextSize(R.dimen.text_size_md),
                    fontFamily = BHoma
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    modifier = Modifier.weight(2f, fill = true),
                    textAlign = TextAlign.Right,
                    text = stringResource(id = R.string.price) ,
                    fontSize = dimenTextSize(R.dimen.text_size_md),
                    fontFamily = BMitra
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductItem() {
    ComposeTrainerTheme {
        ProductItem(
            product = Product(
                id = 1,
                name = "Sample Product",
                barcode = "123456789",
                price = 1000,
                image = null,
                categoryID = 1,
                date = System.currentTimeMillis(),
                stock = 10
            ),
            onEdit = {},
            onDelete = {},
            onIncreaseStock = {},
            onDecreaseStock = {}
        )
    }
}