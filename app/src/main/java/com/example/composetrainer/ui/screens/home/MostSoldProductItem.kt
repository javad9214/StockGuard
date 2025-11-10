package com.example.composetrainer.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductId
import com.example.composetrainer.domain.model.ProductName
import com.example.composetrainer.domain.model.ProductSalesSummary
import com.example.composetrainer.domain.model.ProductSalesSummaryId
import com.example.composetrainer.domain.model.SalesQuantity
import com.example.composetrainer.domain.model.StockQuantity
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.ui.screens.component.CurrencyIcon
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.ui.theme.color.bronze
import com.example.composetrainer.ui.theme.color.customError
import com.example.composetrainer.ui.theme.color.gold
import com.example.composetrainer.ui.theme.color.silver
import com.example.composetrainer.ui.theme.color.success
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.price.PriceValidator.formatPrice
import com.example.composetrainer.utils.str
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun MostSoldProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    productSalesSummary: ProductSalesSummary,
    rank: Int? = null
) {
    val profitMargin =
        productSalesSummary.getProfitMargin().setScale(0, RoundingMode.HALF_UP).toString()

    Log.i(TAG, "MostSoldProductItem: profitMargin ${productSalesSummary.getProfitMargin()}")
    Log.i(TAG, "MostSoldProductItem: profitMargin: $profitMargin")

    val stockStatus = product.getStockStatus()
    val performanceLevel = productSalesSummary.getSalesPerformance()

    ElevatedCard(
        modifier = modifier
            .padding(dimen(R.dimen.space_1))
            .width(dimen(R.dimen.size_8xl))
            .wrapContentHeight(),
        shape = RoundedCornerShape(dimen(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(dimen(R.dimen.space_4))) {

            // Header with rank badge and product name
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
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = dimen(R.dimen.space_10))
                )

                rank?.let { RankBadge(rank = it) }
            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_1)))

            // Sales metrics in a highlighted row
            SalesMetricRow(
                quantity = productSalesSummary.totalSold.value,
                revenue = productSalesSummary.totalRevenue.amount
            )

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

            // Profit information using domain model method
            ProfitInfoRow(
                label = str(R.string.total_profit),
                amount = productSalesSummary.getTotalProfit().amount,
                percentage = profitMargin,
                icon = R.drawable.status_up_bulk,
                isProfitable = productSalesSummary.isProfitable()
            )

            // Stock availability indicator using domain model
            StockIndicator(
                stockLevel = product.stock.value,
                stockStatus = stockStatus,
                minLevel = product.minStockLevel?.value
            )
        }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    val (backgroundColor, icon) = when (rank) {
        1 -> MaterialTheme.colorScheme.gold to R.drawable.crown
        2 -> MaterialTheme.colorScheme.silver to R.drawable.medal_star
        3 -> MaterialTheme.colorScheme.bronze to R.drawable.award
        else -> MaterialTheme.colorScheme.outline to R.drawable.award
    }

    Row(
        modifier = Modifier
            .background(
                color = backgroundColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(dimen(R.dimen.radius_md))
            )
            .padding(
                horizontal = dimen(R.dimen.space_2),
                vertical = dimen(R.dimen.space_1)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Rank $rank",
            modifier = Modifier.size(dimen(R.dimen.size_xs)),
            tint = backgroundColor
        )
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = BMitra,
            color = backgroundColor
        )
    }
}

@Composable
private fun SalesMetricRow(
    quantity: Int,
    revenue: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(dimen(R.dimen.radius_md))
            )
            .padding(dimen(R.dimen.space_3)),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Quantity sold
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
            ) {

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BMitra,
                    color = MaterialTheme.colorScheme.primary
                )

                Icon(
                    painter = painterResource(id = R.drawable.box),
                    contentDescription = str(R.string.products_sold_count),
                    modifier = Modifier.size(dimen(R.dimen.size_sm)),
                    tint = MaterialTheme.colorScheme.primary
                )

            }
            Text(
                text = str(R.string.units_sold),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = BMitra,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Revenue
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
            ) {
                Text(
                    text = formatPrice(revenue.toString()),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                CurrencyIcon(
                    contentDescription = "Rial",
                    modifier = Modifier.size(dimen(R.dimen.size_sm))
                )
            }
            Text(
                text = str(R.string.total_revenue),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = BMitra,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfitInfoRow(
    label: String,
    amount: Long,
    percentage: String,
    icon: Int,
    isProfitable: Boolean
) {
    val profitColor = if (isProfitable)
        MaterialTheme.colorScheme.success
    else
        MaterialTheme.colorScheme.customError

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen(R.dimen.space_1)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(dimen(R.dimen.size_sm)),
                tint = profitColor
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontFamily = BMitra,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
        ) {
            // Profit margin badge
            Text(
                text = "${if (isProfitable) "+" else ""}$percentage%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = BMitra,
                color = profitColor,
                modifier = Modifier
                    .background(
                        color = profitColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
                    )
                    .padding(
                        horizontal = dimen(R.dimen.space_2),
                        vertical = dimen(R.dimen.space_1)
                    )
            )

            Text(
                formatPrice(amount.toString()),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = profitColor
            )
            CurrencyIcon(
                contentDescription = "Rial",
                modifier = Modifier.size(dimen(R.dimen.size_sm))
            )
        }
    }
}

@Composable
private fun StockIndicator(
    stockLevel: Int,
    stockStatus: com.example.composetrainer.domain.model.StockStatus,
    minLevel: Int?
) {
    val (statusText, statusColor) = when (stockStatus) {
        com.example.composetrainer.domain.model.StockStatus.OUT_OF_STOCK ->
            str(R.string.out_of_stock) to MaterialTheme.colorScheme.error

        com.example.composetrainer.domain.model.StockStatus.LOW_STOCK ->
            str(R.string.stock_low) to MaterialTheme.colorScheme.error.copy(alpha = 0.7f)

        com.example.composetrainer.domain.model.StockStatus.OVERSTOCKED ->
            str(R.string.overstocked) to MaterialTheme.colorScheme.secondary

        com.example.composetrainer.domain.model.StockStatus.NORMAL ->
            str(R.string.stock_healthy) to MaterialTheme.colorScheme.tertiary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimen(R.dimen.space_2)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
        ) {
            // Status indicator dot
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .size(dimen(R.dimen.space_2))
                    .clip(CircleShape)
            ) {
                drawCircle(color = statusColor)
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = BMitra,
                color = statusColor
            )
        }

        Text(
            text = "$stockLevel ${str(R.string.units_available)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            fontFamily = BMitra,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MostSoldProductItemPreview() {
    ComposeTrainerTheme {
        Column(verticalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))) {
            val sampleProduct1 = Product(
                id = ProductId(1L),
                name = ProductName("Samsung Galaxy A54"),
                barcode = null,
                price = Money(2500000L),
                costPrice = Money(2000000L),
                description = null,
                image = null,
                subcategoryId = null,
                supplierId = null,
                unit = null,
                stock = StockQuantity(50),
                minStockLevel = StockQuantity(20),
                maxStockLevel = null,
                isActive = true,
                tags = null,
                lastSoldDate = LocalDateTime.now().minusDays(2),
                date = LocalDateTime.now().minusMonths(1),
                synced = true,
                createdAt = LocalDateTime.now().minusMonths(1),
                updatedAt = LocalDateTime.now().minusDays(1)
            )

            val sampleSalesSummary1 = ProductSalesSummary(
                id = ProductSalesSummaryId(1L),
                productId = ProductId(1L),
                date = LocalDate.now(),
                totalSold = SalesQuantity(125),
                totalRevenue = Money(312500000L),
                totalCost = Money(250000000L),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                synced = true
            )

            MostSoldProductItem(
                product = sampleProduct1,
                productSalesSummary = sampleSalesSummary1,
                rank = 1
            )

            // Low stock example
            val sampleProduct2 = Product(
                id = ProductId(2L),
                name = ProductName("iPhone 15 Pro Max"),
                barcode = null,
                price = Money(4500000L),
                costPrice = Money(4000000L),
                description = null,
                image = null,
                subcategoryId = null,
                supplierId = null,
                unit = null,
                stock = StockQuantity(8),
                minStockLevel = StockQuantity(15),
                maxStockLevel = null,
                isActive = true,
                tags = null,
                lastSoldDate = LocalDateTime.now().minusDays(1),
                date = LocalDateTime.now().minusMonths(1),
                synced = true,
                createdAt = LocalDateTime.now().minusMonths(1),
                updatedAt = LocalDateTime.now().minusDays(1)
            )

            val sampleSalesSummary2 = ProductSalesSummary(
                id = ProductSalesSummaryId(2L),
                productId = ProductId(2L),
                date = LocalDate.now(),
                totalSold = SalesQuantity(98),
                totalRevenue = Money(441000000L),
                totalCost = Money(392000000L),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                synced = true
            )

            MostSoldProductItem(
                product = sampleProduct2,
                productSalesSummary = sampleSalesSummary2,
                rank = 2
            )
        }
    }
}