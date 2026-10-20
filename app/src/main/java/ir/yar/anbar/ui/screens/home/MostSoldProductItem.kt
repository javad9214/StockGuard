package ir.yar.anbar.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductId
import ir.yar.anbar.domain.model.ProductName
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.model.ProductSalesSummaryId
import ir.yar.anbar.domain.model.SalesQuantity
import ir.yar.anbar.domain.model.StockQuantity
import ir.yar.anbar.domain.model.StockStatus
import ir.yar.anbar.domain.model.type.Money
import ir.yar.anbar.ui.screens.component.CurrencyIcon
import ir.yar.anbar.ui.theme.BMitra
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.theme.ComposeTrainerTheme
import ir.yar.anbar.ui.theme.color.bronze
import ir.yar.anbar.ui.theme.color.bronzeBg
import ir.yar.anbar.ui.theme.color.customError
import ir.yar.anbar.ui.theme.color.gold
import ir.yar.anbar.ui.theme.color.goldBg
import ir.yar.anbar.ui.theme.color.silver
import ir.yar.anbar.ui.theme.color.silverBg
import ir.yar.anbar.ui.theme.color.success
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.price.PriceValidator.formatPrice
import ir.yar.anbar.utils.str
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

    val stockStatus = product.getStockStatus()

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

            // Rank comes first: in a "most sold" carousel the medal is the
            // primary scan target, the name is secondary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rank?.let { RankBadge(rank = it) }

                Text(
                    text = product.name.value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_lg),
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))

            SalesMetricRow(
                quantity = productSalesSummary.totalSold.value,
                revenue = productSalesSummary.totalRevenue.amount
            )

            ProfitRow(
                amount = productSalesSummary.getTotalProfit().amount,
                marginPercent = profitMargin,
                isProfitable = productSalesSummary.isProfitable()
            )

            // Separates the financial story above from the stock state below
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimen(R.dimen.space_2)),
                thickness = dimen(R.dimen.stroke_dimen_xs),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            StockStatusRow(
                stockLevel = product.stock.value,
                stockStatus = stockStatus,
                minLevel = product.minStockLevel?.value
            )
        }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    val (medalColor, medalContainer, icon) = when (rank) {
        1 -> Triple(
            MaterialTheme.colorScheme.gold,
            MaterialTheme.colorScheme.goldBg,
            R.drawable.crown
        )

        2 -> Triple(
            MaterialTheme.colorScheme.silver,
            MaterialTheme.colorScheme.silverBg,
            R.drawable.medal_star
        )

        3 -> Triple(
            MaterialTheme.colorScheme.bronze,
            MaterialTheme.colorScheme.bronzeBg,
            R.drawable.award
        )

        else -> Triple(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
            R.drawable.award
        )
    }

    Row(
        modifier = Modifier
            .border(
                width = dimen(R.dimen.stroke_dimen_xs),
                color = medalColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(dimen(R.dimen.radius_circle))
            )
            .background(
                color = medalContainer,
                shape = RoundedCornerShape(dimen(R.dimen.radius_circle))
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
            contentDescription = null, // the visible rank number carries the meaning
            modifier = Modifier.size(dimen(R.dimen.size_xs)),
            tint = medalColor
        )
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = BMitra,
            // medal hues fail text contrast on their pastel containers in light
            // mode; the icon + container carry the medal identity instead
            color = MaterialTheme.colorScheme.onSurface
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
            .padding(bottom = dimen(R.dimen.space_2)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetricColumn(
            value = quantity.toString(),
            label = str(R.string.units_sold),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.box),
                contentDescription = null, // the label below the value already names it
                modifier = Modifier.size(dimen(R.dimen.size_sm)),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        VerticalDivider(
            modifier = Modifier
                .padding(horizontal = dimen(R.dimen.space_2))
                .height(dimen(R.dimen.size_md)),
            thickness = dimen(R.dimen.stroke_dimen_xs),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        MetricColumn(
            value = formatPrice(revenue),
            label = str(R.string.total_revenue),
            modifier = Modifier.weight(1f)
        ) {
            CurrencyIcon(
                contentDescription = "Rial",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimen(R.dimen.size_sm))
            )
        }
    }
}

@Composable
private fun MetricColumn(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = BMitra,
                color = MaterialTheme.colorScheme.primary
            )
            trailingIcon()
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = BMitra,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfitRow(
    amount: Long,
    marginPercent: String,
    isProfitable: Boolean
) {
    val profitColor = if (isProfitable)
        MaterialTheme.colorScheme.success
    else
        MaterialTheme.colorScheme.customError

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.status_up_bulk),
                contentDescription = null, // the visible label carries the meaning
                modifier = Modifier.size(dimen(R.dimen.size_sm)),
                tint = profitColor
            )
            Text(
                text = str(R.string.total_profit),
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
            Text(
                text = "${if (isProfitable) "+" else ""}$marginPercent%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = BMitra,
                color = profitColor,
                modifier = Modifier
                    .background(
                        color = profitColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(dimen(R.dimen.radius_circle))
                    )
                    .padding(
                        horizontal = dimen(R.dimen.space_2),
                        vertical = dimen(R.dimen.space_1)
                    )
            )

            Text(
                text = formatPrice(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = BMitra,
                color = profitColor
            )
            CurrencyIcon(
                contentDescription = "Rial",
                tint = profitColor,
                modifier = Modifier.size(dimen(R.dimen.size_sm))
            )
        }
    }
}

@Composable
private fun StockStatusRow(
    stockLevel: Int,
    stockStatus: StockStatus,
    minLevel: Int?
) {
    val (statusText, statusColor) = when (stockStatus) {
        StockStatus.OUT_OF_STOCK ->
            str(R.string.out_of_stock) to MaterialTheme.colorScheme.error

        StockStatus.LOW_STOCK ->
            str(R.string.stock_low) to MaterialTheme.colorScheme.error

        StockStatus.OVERSTOCKED ->
            str(R.string.overstocked) to MaterialTheme.colorScheme.secondary

        StockStatus.NORMAL ->
            str(R.string.stock_healthy) to MaterialTheme.colorScheme.tertiary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(dimen(R.dimen.radius_circle))
                )
                .padding(
                    horizontal = dimen(R.dimen.space_2),
                    vertical = dimen(R.dimen.space_1)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
        ) {
            Box(
                modifier = Modifier
                    .size(dimen(R.dimen.space_2))
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                fontFamily = BMitra,
                color = statusColor
            )
        }

        // "current/min" gives the raw count its reorder context
        val stockText = if (minLevel != null) "$stockLevel/$minLevel" else "$stockLevel"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
        ) {
            Text(
                text = stockText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                fontFamily = BMitra,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = str(R.string.units_available),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = BMitra,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MostSoldProductItemPreview() {
    ComposeTrainerTheme {
        Column(verticalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))) {
            // Rank 1 — profitable, healthy stock
            val sampleProduct1 = Product(
                id = ProductId(1L),
                name = ProductName("گوشی سامسونگ گلکسی A54"),
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

            // Rank 2 — loss-making, low stock
            val sampleProduct2 = Product(
                id = ProductId(2L),
                name = ProductName("گوشی آیفون 15 پرو مکس"),
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
                totalCost = Money(460000000L),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                synced = true
            )

            MostSoldProductItem(
                product = sampleProduct2,
                productSalesSummary = sampleSalesSummary2,
                rank = 2
            )

            // Rank 3 — profitable, out of stock
            val sampleProduct3 = Product(
                id = ProductId(3L),
                name = ProductName("شیائومی ردمی نوت 13"),
                barcode = null,
                price = Money(1400000L),
                costPrice = Money(1200000L),
                description = null,
                image = null,
                subcategoryId = null,
                supplierId = null,
                unit = null,
                stock = StockQuantity(0),
                minStockLevel = StockQuantity(10),
                maxStockLevel = null,
                isActive = true,
                tags = null,
                lastSoldDate = LocalDateTime.now().minusDays(3),
                date = LocalDateTime.now().minusMonths(2),
                synced = true,
                createdAt = LocalDateTime.now().minusMonths(2),
                updatedAt = LocalDateTime.now().minusDays(3)
            )

            val sampleSalesSummary3 = ProductSalesSummary(
                id = ProductSalesSummaryId(3L),
                productId = ProductId(3L),
                date = LocalDate.now(),
                totalSold = SalesQuantity(64),
                totalRevenue = Money(89600000L),
                totalCost = Money(76800000L),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                synced = true
            )

            MostSoldProductItem(
                product = sampleProduct3,
                productSalesSummary = sampleSalesSummary3,
                rank = 3
            )
        }
    }
}
