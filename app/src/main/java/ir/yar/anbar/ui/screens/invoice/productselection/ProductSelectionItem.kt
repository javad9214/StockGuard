package ir.yar.anbar.ui.screens.invoice.productselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.ui.screens.component.CurrencyIcon
import ir.yar.anbar.ui.theme.BRoya
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.price.PriceValidator
import ir.yar.anbar.utils.str

@Composable
fun ProductSelectionItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(dimen(R.dimen.radius_sm))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = dimen(R.dimen.space_2),
                        horizontal = dimen(R.dimen.space_4)
                    ),
                text = product.name.value,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimen(R.dimen.space_4),
                        vertical = dimen(R.dimen.space_4)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {


                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CurrencyIcon(
                        contentDescription = "Rial",
                        modifier = Modifier
                            .size(dimen(R.dimen.size_sm))
                    )
                    Text(
                        modifier = Modifier.padding(start = dimen(R.dimen.space_1)),
                        text = PriceValidator.formatPrice(product.price),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    text = str(R.string.stock_with_value, product.stock.value),
                    fontFamily = BRoya,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (product.stock.value > 0)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )

            }

        }

    }
}
