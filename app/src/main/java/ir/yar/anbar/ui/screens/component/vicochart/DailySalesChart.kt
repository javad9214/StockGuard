package ir.yar.anbar.ui.screens.component.vicochart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
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
import ir.yar.anbar.ui.viewmodels.DailySalesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

@Composable
fun DailySalesChart(
    dailySalesData: List<DailySalesData>,
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    // Update chart data when dailySalesData changes
    LaunchedEffect(dailySalesData) {
        withContext(Dispatchers.Default) {
            if (dailySalesData.isEmpty()) {
                // Show sample data if no real data available
                modelProducer.runTransaction {
                    columnSeries {
                        series(
                            x = listOf(1, 2, 3, 4, 5, 6, 7),
                            y = listOf(0, 0, 0, 0, 0, 0, 0)
                        )
                    }
                }
            } else {
                modelProducer.runTransaction {
                    columnSeries {
                        series(
                            x = dailySalesData.indices.map { it + 1 },
                            y = dailySalesData.map { it.sales / 1000.0 } // Convert to thousands
                        )
                    }
                }
            }
        }
    }

    CartesianChartHost(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
    )
}