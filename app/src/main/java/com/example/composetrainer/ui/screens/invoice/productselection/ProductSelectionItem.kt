package com.example.composetrainer.ui.screens.invoice.productselection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.composetrainer.R
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.ui.theme.BRoya
import com.example.composetrainer.utils.PriceValidator
import com.example.composetrainer.utils.dimen

@Composable
fun ProductSelectionItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showStock: Boolean = true
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen(R.dimen.space_4), vertical = dimen(R.dimen.space_4)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.toman),
                    contentDescription = "Date",
                    modifier = Modifier
                        .size(dimen(R.dimen.size_sm))
                )

                Text(
                    modifier = Modifier.padding(start = dimen(R.dimen.space_1)),
                    text = PriceValidator.formatPrice(product.price.toString()),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.wrapContentWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (showStock) {
                    Text(
                        text = "${stringResource(R.string.stock)} : ${product.stock}",
                        fontFamily = BRoya,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (product.stock > 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductSelectionItemPreview() {
    val sampleProduct = Product(
        id = 1L,
        name = "Sample Product",
        barcode = null,
        price = 250000L,
        image = null,
        categoryID = null,
        date = System.currentTimeMillis(),
        stock = 5
    )

    ProductSelectionItem(
        product = sampleProduct,
        onClick = {}
    )
}