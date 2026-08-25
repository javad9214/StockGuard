package ir.yar.anbar.ui.screens.component.vicochart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import java.util.Locale

/**
 * Column chart visualizing the top products of the selected period.
 * The data is pushed into [modelProducer] by the ViewModel; this composable only renders it.
 *
 * Colors are derived from MaterialTheme.colorScheme via [rememberM3VicoTheme], so the chart
 * follows the app's light/dark theme. Data changes animate per the host's animation spec.
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

    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            modifier = modifier
                .fillMaxWidth()
                .height(250.dp),
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(valueFormatter = startAxisValueFormatter),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = bottomAxisValueFormatter)
            ),
            modelProducer = modelProducer,
            animationSpec = tween(durationMillis = CHART_ANIMATION_DURATION_MS, easing = FastOutSlowInEasing)
        )
    }
}

private const val CHART_ANIMATION_DURATION_MS = 450

/** Formats money values compactly (e.g. 12.5M, 300K) to keep the axis readable. */
private fun formatCompactMoney(value: Double): String = when {
    value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000)
    value >= 1_000 -> String.format(Locale.US, "%.0fK", value / 1_000)
    else -> String.format(Locale.US, "%.0f", value)
}
