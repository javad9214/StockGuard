package ir.yar.anbar.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import ir.yar.anbar.R
import ir.yar.anbar.ui.screens.component.DatePickerBottomDialog
import ir.yar.anbar.ui.screens.component.vicochart.TopProductsColumnChart
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.viewmodels.AnalyzeMetric
import ir.yar.anbar.ui.viewmodels.AnalyzeUiState
import ir.yar.anbar.ui.viewmodels.AnalyzeViewModel
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzeScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalyzeViewModel = hiltViewModel()
) {
    val analyzeState by viewModel.uiState.collectAsState()
    val selectedTimeRange = analyzeState.selectedTimeRange
    val loadInFlight = analyzeState.isLoading || analyzeState.isRefreshing

    var showDatePickerBottomSheet by remember { mutableStateOf(false) }
    val datePickerSheetState = rememberModalBottomSheetState()

    Column(modifier = modifier.fillMaxWidth()) {
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

            IconButton(
                onClick = { viewModel.refresh() },
                enabled = !loadInFlight
            ) {
                if (loadInFlight) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimen(R.dimen.size_sm)),
                        strokeWidth = dimen(R.dimen.stroke_dimen_xs)
                    )
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = str(R.string.refresh))
                }
            }
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_5)))

        // Date range selector - same pattern as HomeScreen
        ElevatedCard(
            modifier = Modifier
                .height(dimen(R.dimen.size_lg))
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(dimen(R.dimen.radius_xxl)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            onClick = { showDatePickerBottomSheet = true }
        ) {
            Row(
                modifier = Modifier.padding(dimen(R.dimen.space_1)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = dimen(R.dimen.space_1), start = dimen(R.dimen.space_4)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = str(selectedTimeRange.getResourceId()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_lg)
                    )
                }

                Spacer(modifier = Modifier.width(dimen(R.dimen.space_1)))

                Icon(
                    modifier = Modifier.padding(end = dimen(R.dimen.space_1)),
                    painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                    contentDescription = str(R.string.select_time_range),
                )
            }
        }

        if (showDatePickerBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showDatePickerBottomSheet = false },
                sheetState = datePickerSheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStart = dimen(R.dimen.radius_lg),
                    topEnd = dimen(R.dimen.radius_lg)
                )
            ) {
                DatePickerBottomDialog(
                    selectedItem = selectedTimeRange,
                    onNewSelected = { timeRange ->
                        viewModel.onTimeRangeSelected(timeRange)
                        showDatePickerBottomSheet = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

        // Metric toggle — both datasets are already in ViewModel state; switching is instant
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = dimen(R.dimen.space_4))
        ) {
            SegmentedButton(
                selected = analyzeState.selectedMetric == AnalyzeMetric.TOP_SELLING,
                onClick = { viewModel.onMetricSelected(AnalyzeMetric.TOP_SELLING) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                label = {
                    Text(
                        text = str(R.string.most_sold_products),
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_md)
                    )
                }
            )
            SegmentedButton(
                selected = analyzeState.selectedMetric == AnalyzeMetric.TOP_PROFITABLE,
                onClick = { viewModel.onMetricSelected(AnalyzeMetric.TOP_PROFITABLE) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                label = {
                    Text(
                        text = str(R.string.most_profitable_products),
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_md)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

        // Inline progress while re-fetching with data on screen — the chart below stays visible
        if (analyzeState.isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen(R.dimen.space_4))
            )

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))
        }

        // Crossfade between spinner / placeholders / chart so state swaps aren't abrupt
        AnimatedContent(
            targetState = analyzeState.chartSection(),
            transitionSpec = {
                fadeIn(tween(CHART_SECTION_ANIMATION_MS)) togetherWith
                    fadeOut(tween(CHART_SECTION_ANIMATION_MS))
            },
            label = "chartSection"
        ) { section ->
            when (section) {
                ChartSection.ERROR -> ChartPlaceholder(
                    text = str(R.string.error_prefix) + analyzeState.error,
                    onRetry = { viewModel.refresh() }
                )

                ChartSection.LOADING -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen(R.dimen.size_8xl)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                ChartSection.EMPTY -> ChartPlaceholder(text = str(R.string.no_data_for_period))

                ChartSection.CONTENT -> TopProductsColumnChart(
                    modelProducer = viewModel.modelProducer,
                    productLabels = analyzeState.productLabels,
                    modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
                )
            }
        }

        Spacer(modifier = Modifier.height(dimen(R.dimen.space_8)))
    }
}

@Composable
private fun ChartPlaceholder(
    text: String,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimen(R.dimen.size_8xl)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.monitoring_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_md),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))

                Button(onClick = onRetry) {
                    Text(str(R.string.retry))
                }
            }
        }
    }
}

/** Which branch of the chart area is displayed; the [AnimatedContent] key. */
private enum class ChartSection { ERROR, LOADING, EMPTY, CONTENT }

private fun AnalyzeUiState.chartSection(): ChartSection = when {
    error != null -> ChartSection.ERROR
    isLoading && isEmpty -> ChartSection.LOADING
    isEmpty -> ChartSection.EMPTY
    else -> ChartSection.CONTENT
}

private const val CHART_SECTION_ANIMATION_MS = 200
