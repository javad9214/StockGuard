package ir.yar.anbar.ui.screens.component.vicochart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun JetpackComposeBasic2ColumnChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {
    // Sample data - we'll replace this with real data later
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                // Simple sample data: 7 data points
                columnSeries {
                    series(
                        x = listOf(1, 2, 3, 4, 5, 6, 7),
                        y = listOf(4, 12, 8, 16, 10, 14, 6)
                    )
                }
            }
        }
    }

    CartesianChartHost(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        chart = rememberCartesianChart(
            // Use default column layer (no custom columnProvider) to match API
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
    )
}