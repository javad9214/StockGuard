package com.example.composetrainer.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductId
import com.example.composetrainer.domain.model.ProductName
import com.example.composetrainer.domain.model.StockQuantity
import com.example.composetrainer.domain.model.StockStatus
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.ui.screens.component.CurrencyIcon
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.utils.price.PriceValidator.formatPrice
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str
import java.time.LocalDateTime

@Composable
fun LowStockProductItem(
    modifier: Modifier = Modifier,
    product: Product
) {
    // Use domain model methods for business logic
    val stockStatus = product.getStockStatus()
    val needsRestock = product.needsRestock()
    val recommendedQuantity = product.getRecommendedOrderQuantity()
    val daysSinceLastSold = product.getDaysSinceLastSold()
    Log.i(TAG, "LowStockProductItem: daysSinceLastSold $daysSinceLastSold")
    Log.i(TAG, "LowStockProductItem: lastSold ${product.lastSoldDate}")

    val (urgencyColor, urgencyLabel) = when (stockStatus) {
        StockStatus.OUT_OF_STOCK ->
            MaterialTheme.colorScheme.error to str(R.string.out_of_stock)

        StockStatus.LOW_STOCK ->
            MaterialTheme.colorScheme.tertiary to str(R.string.critical)

        else ->
            MaterialTheme.colorScheme.secondary to str(R.string.low)
    }

    ElevatedCard(
        modifier = modifier
            .padding(dimen(R.dimen.space_1))
            .width(dimen(R.dimen.size_8xl))
            .wrapContentHeight(),
        shape = RoundedCornerShape(dimen(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(dimen(R.dimen.space_2))) {

            // Product name with urgency badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.name.value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = dimen(R.dimen.space_10))
                )

                // Urgency badge
                if (needsRestock) {
                    Text(
                        text = urgencyLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = BMitra,
                        color = urgencyColor,
                        modifier = Modifier
                            .background(
                                color = urgencyColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
                            )
                            .padding(
                                horizontal = dimen(R.dimen.space_2),
                                vertical = dimen(R.dimen.space_1)
                            )
                    )
                }
            }

            // Current stock with domain logic
            StockInfoRow(
                label = str(R.string.current_stock),
                value = product.stock.value.toString(),
                icon = R.drawable.box,
                iconDescription = str(R.string.current_stock),
                textColor = urgencyColor
            )

            // Minimum stock level
            product.minStockLevel?.let { minLevel ->
                StockInfoRow(
                    label = str(R.string.min_stock_level),
                    value = minLevel.value.toString(),
                    icon = R.drawable.error_24px,
                    iconDescription = str(R.string.min_stock_level)
                )
            }

            // Recommended reorder using domain method
            recommendedQuantity?.let { quantity ->
                product.costPrice.let { cost ->
                    val reorderValue = cost.amount * quantity.value
                    StockInfoRow(
                        label = str(R.string.reorder_cost),
                        value = reorderValue.toString(),
                        icon = R.drawable.dollar_circle,
                        iconDescription = str(R.string.reorder_cost),
                        isAmount = true,
                        badge = "${quantity.value} ${str(R.string.units)}"
                    )
                }
            }

            // Days since last sold using domain method
            daysSinceLastSold?.let { days ->
                if (days > 0) {
                    val daysText = if (days == 1L)
                        str(R.string.yesterday)
                    else
                        "$days ${str(R.string.days_ago)}"

                    StockInfoRow(
                        label = str(R.string.last_sold),
                        value = daysText,
                        icon = R.drawable.clock,
                        iconDescription = str(R.string.last_sold),
                        textColor = if (days > 30)
                            MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } ?: run {
                // Product has never been sold
                if (product.getProductAge() > 7) {
                    StockInfoRow(
                        label = str(R.string.last_sold),
                        value = str(R.string.never_sold),
                        icon = R.drawable.clock,
                        iconDescription = str(R.string.last_sold),
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Show if product is dead stock
            if (product.isDeadStock()) {
                DeadStockWarning()
            }
        }
    }
}

@Composable
private fun StockInfoRow(
    label: String,
    value: String,
    icon: Int,
    iconDescription: String,
    isAmount: Boolean = false,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer,
    badge: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen(R.dimen.space_2)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            fontFamily = BMitra,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
        ) {
            badge?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    fontFamily = BMitra,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
                        )
                        .padding(
                            horizontal = dimen(R.dimen.space_1),
                            vertical = dimen(R.dimen.space_4)
                        )
                )
            }

            if (isAmount) {
                Text(
                    formatPrice(value),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                CurrencyIcon(
                    contentDescription = "Rial",
                    modifier = Modifier
                        .size(dimen(R.dimen.size_sm))
                        .padding(start = dimen(R.dimen.space_1))
                )
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BMitra,
                    color = textColor
                )
            }

            Icon(
                painter = painterResource(id = icon),
                contentDescription = iconDescription,
                modifier = Modifier
                    .size(dimen(R.dimen.size_sm))
                    .padding(start = dimen(R.dimen.space_1)),
                tint = if (isAmount) MaterialTheme.colorScheme.primary else textColor
            )
        }
    }
}

@Composable
private fun DeadStockWarning() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
            )
            .padding(dimen(R.dimen.space_2)),
        horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.warning_24px),
            contentDescription = str(R.string.dead_stock_warning),
            modifier = Modifier.size(dimen(R.dimen.size_sm)),
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = str(R.string.dead_stock_warning),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            fontFamily = BMitra,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LowStockProductItemPreview() {
    ComposeTrainerTheme {
        Column {
            // Critical stock level
            val criticalProduct = Product(
                id = ProductId(1L),
                name = ProductName("iPhone 15 Pro Max 256GB"),
                barcode = null,
                price = Money(45000000L),
                costPrice = Money(40000000L),
                description = null,
                image = null,
                subcategoryId = null,
                supplierId = null,
                unit = null,
                stock = StockQuantity(2),
                minStockLevel = StockQuantity(10),
                maxStockLevel = StockQuantity(50),
                isActive = true,
                tags = null,
                lastSoldDate = LocalDateTime.now().minusDays(1),
                date = LocalDateTime.now().minusMonths(1),
                synced = true,
                createdAt = LocalDateTime.now().minusMonths(1),
                updatedAt = LocalDateTime.now().minusDays(1)
            )

            LowStockProductItem(product = criticalProduct)

            // Dead stock example
            val deadStockProduct = Product(
                id = ProductId(3L),
                name = ProductName("Old Model Phone"),
                barcode = null,
                price = Money(15000000L),
                costPrice = Money(12000000L),
                description = null,
                image = null,
                subcategoryId = null,
                supplierId = null,
                unit = null,
                stock = StockQuantity(5),
                minStockLevel = StockQuantity(10),
                maxStockLevel = StockQuantity(30),
                isActive = true,
                tags = null,
                lastSoldDate = LocalDateTime.now().minusMonths(7),
                date = LocalDateTime.now().minusMonths(10),
                synced = true,
                createdAt = LocalDateTime.now().minusMonths(10),
                updatedAt = LocalDateTime.now().minusMonths(7)
            )

            LowStockProductItem(product = deadStockProduct)
        }
    }
}
