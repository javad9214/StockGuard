package ir.yar.anbar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ir.yar.anbar.R
import ir.yar.anbar.ui.screens.component.JetpackComposeBasicColumnChart
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.viewmodels.AnalyzeViewModel
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzeScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalyzeViewModel = hiltViewModel()
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                str(R.string.sales_analyse),
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_xl)
            )

            IconButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Default.Refresh, contentDescription = "refresh")
            }
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_5)))


        JetpackComposeBasicColumnChart(modelProducer)
    }

}





