package ir.yar.anbar.ui.screens.setting

// imports
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.yar.anbar.R
import ir.yar.anbar.ui.theme.AppFont.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockRunoutLimitSelector(
    limit: Int,
    onLimitChange: (Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen(R.dimen.space_2), vertical = dimen(R.dimen.space_2)),
        shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen(R.dimen.space_6), vertical = dimen(R.dimen.space_4)),
            verticalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_3))
        ) {
            Text(
                text = str(R.string.stock_runout_limit),
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_lg),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Display the current value with a bubble above the thumb
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "$limit",
                    fontSize = dimenTextSize(R.dimen.text_size_md),
                    fontFamily = BKoodak,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            Slider(
                value = limit.toFloat(),
                onValueChange = { onLimitChange(it.toInt()) },
                valueRange = 0f..50f,
                steps = 49,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledActiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    disabledInactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                ),
                modifier = Modifier
                    .height(36.dp)
                    .padding(vertical = dimen(R.dimen.space_1))
            )

        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewStockRunoutLimitSelector() {
    var tempLimit by remember { mutableStateOf(10) }

    MaterialTheme {
        StockRunoutLimitSelector(
            limit = tempLimit,
            onLimitChange = { tempLimit = it }
        )
    }
}

