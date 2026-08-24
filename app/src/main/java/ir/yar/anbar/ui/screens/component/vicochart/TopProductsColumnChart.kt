package ir.yar.anbar.ui.screens.component.vicochart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import java.util.Locale

/**
 * Column chart visualizing the top-selling products of the selected period.
 * The data is pushed into [modelProducer] by the ViewModel; this composable only renders it.
 *
 * @param productLabels labels for the bottom axis, indexed by chart entry x value.
 */
@Composable
fun TopProductsColumnChart(
    modelProducer: CartesianChartModelProducer,
    productLabels: List<String>,
    modifier: Modifier = Modifier
) {
    val bottomAxisValueFormatter = remember(productLabels) {
        CartesianValueFormatter { _, value, _ ->
            productLabels.getOrElse(value.toInt()) { "" }
        }
    }
    val startAxisValueFormatter = remember {
        CartesianValueFormatter { _, value, _ -> formatCompactMoney(value) }
    }

    CartesianChartHost(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(valueFormatter = startAxisValueFormatter),
            bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = bottomAxisValueFormatter)
        ),
        modelProducer = modelProducer
    )
}

/** Formats money values compactly (e.g. 12.5M, 300K) to keep the axis readable. */
private fun formatCompactMoney(value: Double): String = when {
    value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000)
    value >= 1_000 -> String.format(Locale.US, "%.0fK", value / 1_000)
    else -> String.format(Locale.US, "%.0f", value)
}
