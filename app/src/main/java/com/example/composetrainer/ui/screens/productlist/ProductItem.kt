package com.example.composetrainer.ui.screens.productlist

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Barcode
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductDescription
import com.example.composetrainer.domain.model.ProductId
import com.example.composetrainer.domain.model.ProductName
import com.example.composetrainer.domain.model.StockQuantity
import com.example.composetrainer.domain.model.SubcategoryId
import com.example.composetrainer.domain.model.SupplierId
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.domain.model.ProductUnit
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.utils.PriceValidator
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import java.time.LocalDateTime

@Composable
fun ProductItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onIncreaseStock: () -> Unit,
    onDecreaseStock: () -> Unit,
    onProductClick: () -> Unit = {}
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var showMenu by remember { mutableStateOf(false) }

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

                // Stock Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = product.stock.value.toString(),
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
                        text = PriceValidator.formatPrice(product.price.amount.toString()),
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        fontFamily = BHoma
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        modifier = Modifier.weight(2f, fill = true),
                        textAlign = TextAlign.Right,
                        text = stringResource(id = R.string.price),
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        fontFamily = BMitra
                    )
                }

            }
        }
    }
}

// Add this Preview function at the bottom of your ProductItem.kt file
// or in a separate preview file.

@Preview(showBackground = true, name = "Product Item Preview")
@Composable
fun ProductItemPreview() {
    // All required fields filled with sample/mock values for preview
    val mockProduct = Product(
        id = ProductId(1),
        name = ProductName("Sample Product Name"),
        barcode = Barcode("1234567890123"),
        price = Money(129990L),
        costPrice = Money(120000L),
        description = ProductDescription("This is a great sample product for preview."),
        image = null,
        subcategoryId = SubcategoryId(4),
        supplierId = SupplierId(2),
        unit = ProductUnit("pcs"),
        stock = StockQuantity(30),
        minStockLevel = StockQuantity(5),
        maxStockLevel = StockQuantity(100),
        isActive = true,
        tags = null,
        lastSoldDate = null,
        date = LocalDateTime.now(),
        synced = true,
        createdAt = LocalDateTime.now().minusDays(20),
        updatedAt = LocalDateTime.now()
    )
    ComposeTrainerTheme {
        ProductItem(
            product = mockProduct,
            onEdit = { /* Preview: Edit clicked */ },
            onDelete = { /* Preview: Delete clicked */ },
            onIncreaseStock = { /* Preview: Increase stock clicked */ },
            onDecreaseStock = { /* Preview: Decrease stock clicked */ },
            onProductClick = { /* Preview: Product clicked */ }
        )
    }
}

@Preview(showBackground = true, name = "Product Item Without Barcode Preview")
@Composable
fun ProductItemWithoutBarcodePreview() {
    // All required Product fields filled with valid dummy data for preview
    val mockProductNoBarcode = Product(
        id = ProductId(2),
        name = ProductName("Another Product (No Barcode)"),
        barcode = null, // Explicitly no barcode
        price = Money(75000L), // e.g., 750.00
        costPrice = Money(50000L), // dummy cost
        description = ProductDescription("This product doesn't have a barcode."),
        image = null,
        subcategoryId = SubcategoryId(3), // sample subcategory
        supplierId = SupplierId(1),
        unit = ProductUnit("pcs"),
        stock = StockQuantity(5),
        minStockLevel = StockQuantity(1),
        maxStockLevel = StockQuantity(20),
        isActive = true,
        tags = null,
        lastSoldDate = null,
        date = LocalDateTime.now(),
        synced = true,
        createdAt = LocalDateTime.now().minusDays(10),
        updatedAt = LocalDateTime.now()
    )

    ComposeTrainerTheme {
        ProductItem(
            product = mockProductNoBarcode,
            onEdit = { },
            onDelete = { },
            onIncreaseStock = { },
            onDecreaseStock = { },
            onProductClick = { }
        )
    }
}
