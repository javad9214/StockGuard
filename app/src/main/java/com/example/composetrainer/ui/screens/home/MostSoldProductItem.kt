package com.example.composetrainer.ui.screens.home

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
import com.example.composetrainer.domain.model.ProductSalesSummary
import com.example.composetrainer.domain.model.ProductSalesSummaryId
import com.example.composetrainer.domain.model.SalesQuantity
import com.example.composetrainer.domain.model.StockQuantity
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.theme.ComposeTrainerTheme
import com.example.composetrainer.utils.price.PriceValidator.formatPrice
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun MostSoldProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    productSalesSummary: ProductSalesSummary
) {


    ElevatedCard(
        modifier = modifier
            .padding(dimen(R.dimen.space_1))
            .width(dimen(R.dimen.size_8xl))
            .wrapContentHeight(),
        shape = RoundedCornerShape(dimen(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(dimen(R.dimen.space_2))) {

            Text(
                text = product.name.value,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_lg),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.heightIn(min = dimen(R.dimen.space_10))
            )


            InfoRow(
                label = str(R.string.products_sold_count),
                value = productSalesSummary.totalSold.value.toString(),
                icon = R.drawable.box,
                iconDescription = str(R.string.products_sold_count)
            )

            InfoRow(
                label = str(R.string.total_profit),
                value = productSalesSummary.totalRevenue.amount.toString(),
                icon = R.drawable.status_up_bulk,
                iconDescription = str(R.string.total_profit),
                isAmount = true
            )

            InfoRow(
                label = str(R.string.total_amount),
                value = productSalesSummary.totalCost.amount.toString(),
                icon = R.drawable.dollar_circle,
                iconDescription = str(R.string.total_amount),
                isAmount = true
            )

        }
    }
}


@Composable
private fun InfoRow(
    label: String,
    value: String,
    icon: Int,
    iconDescription: String,
    isAmount: Boolean = false
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAmount) {
                Text(
                    formatPrice(value),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    painter = painterResource(id = R.drawable.toman),
                    contentDescription = "toman",
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Icon(
                painter = painterResource(id = icon),
                contentDescription = iconDescription,
                modifier = Modifier
                    .size(dimen(R.dimen.size_sm))
                    .padding(start = dimen(R.dimen.space_1))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MostSoldProductItemPreview() {
    ComposeTrainerTheme {
        val sampleProduct = Product(
            id = ProductId(1L),
            name = ProductName("Samsung Galaxy A54 blah blah blah blah and bluh"),
            barcode = null,
            price = Money(2500000L), // 25,000 toman in cents
            costPrice = Money(2000000L), // 20,000 toman in cents
            description = null,
            image = null,
            subcategoryId = null,
            supplierId = null,
            unit = null,
            stock = StockQuantity(50),
            minStockLevel = null,
            maxStockLevel = null,
            isActive = true,
            tags = null,
            lastSoldDate = LocalDateTime.now().minusDays(2),
            date = LocalDateTime.now().minusMonths(1),
            synced = true,
            createdAt = LocalDateTime.now().minusMonths(1),
            updatedAt = LocalDateTime.now().minusDays(1)
        )

        val sampleSalesSummary = ProductSalesSummary(
            id = ProductSalesSummaryId(1L),
            productId = ProductId(1L),
            date = LocalDate.now(),
            totalSold = SalesQuantity(125),
            totalRevenue = Money(312500000L), // 3,125,000 toman in cents
            totalCost = Money(250000000L), // 2,500,000 toman in cents
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            synced = true
        )

        MostSoldProductItem(
            product = sampleProduct,
            productSalesSummary = sampleSalesSummary
        )
    }
}
