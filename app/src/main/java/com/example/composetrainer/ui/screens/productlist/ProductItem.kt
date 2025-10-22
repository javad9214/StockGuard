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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.example.composetrainer.domain.model.ProductUnit
import com.example.composetrainer.domain.model.StockQuantity
import com.example.composetrainer.domain.model.SubcategoryId
import com.example.composetrainer.domain.model.SupplierId
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.ui.screens.component.CurrencyIcon
import com.example.composetrainer.ui.theme.BHoma
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.theme.googleGreen
import com.example.composetrainer.ui.theme.orangeRed
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.price.PriceValidator
import com.example.composetrainer.utils.str
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
                modifier = Modifier.padding(dimen(R.dimen.space_4))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            modifier = Modifier
                                .clickable { showMenu = true }
                        )

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

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))


                // Barcode
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                    Spacer(modifier = Modifier.width(dimen(R.dimen.space_4)))

                    Icon(
                        painter = painterResource(id = R.drawable.barcode_24px),
                        contentDescription = "barcode"
                    )
                }

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                // Stock Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        modifier = Modifier.padding(end = dimen(R.dimen.space_4)),
                        text = product.stock.value.toString(),
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        fontFamily = BHoma
                    )

                    Text(
                        text = str(R.string.stock),
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        fontFamily = BHoma
                    )

                }

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Sale Price
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = str(R.string.sale_price),
                                fontSize = dimenTextSize(R.dimen.text_size_sm),
                                textAlign = TextAlign.Center,
                                fontFamily = Beirut_Medium,
                                color = googleGreen
                            )

                            Spacer(modifier = Modifier.width(dimen(R.dimen.space_1)))

                            Icon(
                                modifier = Modifier
                                    .size(dimen(R.dimen.size_xs))
                                    .rotate(90f),
                                tint = googleGreen,
                                painter = painterResource(id = R.drawable.output_circle_24px),
                                contentDescription = "Sale Icon"
                            )
                        }

                        Row {
                            CurrencyIcon(
                                contentDescription = "Rial",
                                modifier = Modifier.size(dimen(R.dimen.size_sm))
                            )
                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = PriceValidator.formatPrice(product.price.amount.toString()),
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                fontFamily = BHoma
                            )
                        }
                    }

                    // Cost Price
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {

                            Text(
                                text = str(R.string.cost_price),
                                fontSize = dimenTextSize(R.dimen.text_size_sm),
                                textAlign = TextAlign.Center,
                                fontFamily = Beirut_Medium,
                                color = orangeRed
                            )
                            Spacer(modifier = Modifier.width(dimen(R.dimen.space_1)))

                            Icon(
                                modifier = Modifier
                                    .size(dimen(R.dimen.size_xs))
                                    .rotate(90f),
                                painter = painterResource(id = R.drawable.input_circle_24px),
                                contentDescription = "Sale Icon",
                                tint = orangeRed
                            )
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(end = dimen(R.dimen.space_1)),
                            horizontalArrangement = Arrangement.End
                        ){
                            CurrencyIcon(
                                contentDescription = "Rial",
                                modifier = Modifier.size(dimen(R.dimen.size_sm))
                            )
                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = PriceValidator.formatPrice(product.costPrice.amount.toString()),
                                fontSize = dimenTextSize(R.dimen.text_size_sm), // Smaller
                                fontFamily = BHoma
                            )
                        }
                    }
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
