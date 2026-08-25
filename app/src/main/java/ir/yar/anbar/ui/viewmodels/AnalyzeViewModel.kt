package ir.yar.anbar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.usecase.sales.GetTopProfitableProductsUseCase
import ir.yar.anbar.domain.usecase.sales.GetTopSellingProductsUseCase
import ir.yar.anbar.ui.viewmodels.home.ProductWithSummary
import ir.yar.anbar.utils.dateandtime.TimeRange
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Dedicated ViewModel for the Analyze screen.
 *
 * Owns the selected [TimeRange] and [AnalyzeMetric] and fetches the top-selling /
 * top-profitable products directly from the domain layer, independently of the Home
 * screen, and maps them into the format required by Vico (chart entries + axis labels).
 * Both rankings are kept in state, so switching the chart metric is instant (no refetch).
 */
@HiltViewModel
class AnalyzeViewModel @Inject constructor(
    private val getTopSellingProductsUseCase: GetTopSellingProductsUseCase,
    private val getTopProfitableProductsUseCase: GetTopProfitableProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyzeUiState())
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()

    /** Owns the chart data so the screen stays UI-only. */
    val modelProducer = CartesianChartModelProducer()

    /** Collectors of the currently loaded period; cancelled when the period changes. */
    private var loadJob: Job? = null

    /** Latest chart update; cancelled when a newer dataset/metric is pushed. */
    private var chartJob: Job? = null

    init {
        load(_uiState.value.selectedTimeRange)
    }

    /**
     * Updates the period and reloads. Selecting the current period again is a no-op,
     * unless the previous load failed — re-picking it then counts as a retry.
     */
    fun onTimeRangeSelected(timeRange: TimeRange) {
        val current = _uiState.value
        if (timeRange != current.selectedTimeRange || current.error != null) {
            load(timeRange)
        }
    }

    /** Reloads the currently selected period. */
    fun refresh() {
        load(_uiState.value.selectedTimeRange)
    }

    /** Switches which ranking feeds the chart; both datasets are kept in state, so no refetch. */
    fun onMetricSelected(metric: AnalyzeMetric) {
        if (metric == _uiState.value.selectedMetric) return
        _uiState.update { it.copy(selectedMetric = metric) }
        refreshChart()
    }

    private fun load(timeRange: TimeRange) {
        // Cancel the previous period's collectors so stale emissions can't overwrite newer data
        loadJob?.cancel()
        // Reset any previous failure synchronously, before the fetch is dispatched, so a
        // stale error never lingers into a new load attempt. Exactly one loading flag is set:
        // full-screen loading only when there is nothing to show yet, subtle refresh otherwise.
        _uiState.update {
            val hasVisibleData = it.activeProducts.isNotEmpty()
            it.copy(
                selectedTimeRange = timeRange,
                isLoading = !hasVisibleData,
                isRefreshing = hasVisibleData,
                error = null
            )
        }
        loadJob = viewModelScope.launch {

            // Single combined collector: its first emission fires only after BOTH sources have
            // emitted, so isLoading is cleared exactly once — never by one fetch while the
            // other is still running. Both lists also switch to the new period atomically.
            combine(
                getTopSellingProductsUseCase(timeRange),
                getTopProfitableProductsUseCase(timeRange)
            ) { topSelling, topProfitable ->
                combineWithProducts(topSelling.first, topSelling.second) to
                    combineWithProducts(topProfitable.first, topProfitable.second)
            }.collectWithStateUpdates { (topSelling, topProfitable) ->
                _uiState.update {
                    it.copy(
                        topSellingProducts = topSelling,
                        topProfitableProducts = topProfitable,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
                refreshChart()
            }
        }
    }

    /** Re-feeds [modelProducer] from the dataset active in state — no fetch involved. */
    private fun refreshChart() {
        val state = _uiState.value
        // Cancel the pending update so a slow older transaction can't overwrite a newer one
        chartJob?.cancel()
        chartJob = viewModelScope.launch {
            updateChart(state.activeProducts, state.selectedMetric)
        }
    }

    /** Collects [this] flow into [action], surfacing failures as UI state instead of crashing. */
    private suspend fun <T> Flow<T>.collectWithStateUpdates(action: suspend (T) -> Unit) {
        try {
            collect { action(it) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, isLoading = false, isRefreshing = false) }
        }
    }

    /** Joins each summary with its product and ranks it by the use case's sort order. */
    private fun combineWithProducts(
        summaries: List<ProductSalesSummary>,
        products: List<Product>
    ): List<ProductWithSummary> {
        val productsById = products.associateBy { it.id }
        return summaries.mapIndexedNotNull { index, summary ->
            productsById[summary.productId]?.let { product ->
                ProductWithSummary(product = product, summary = summary, rank = index + 1)
            }
        }
    }

    /** Maps the ranked products into Vico entries (revenue or profit per product, in display units). */
    private suspend fun updateChart(products: List<ProductWithSummary>, metric: AnalyzeMetric) {
        if (products.isEmpty()) return

        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                columnSeries {
                    series(
                        x = products.map { it.rank - 1 },
                        y = products.map { product ->
                            val amount = when (metric) {
                                AnalyzeMetric.TOP_SELLING -> product.summary.totalRevenue
                                AnalyzeMetric.TOP_PROFITABLE -> product.summary.getTotalProfit()
                            }
                            amount.toDisplayAmount()
                        }
                    )
                }
            }
        }
    }
}

/** Which product ranking feeds the Analyze chart. */
enum class AnalyzeMetric {
    TOP_SELLING,
    TOP_PROFITABLE
}

data class AnalyzeUiState(
    val selectedTimeRange: TimeRange = TimeRange.THIS_MONTH,
    val selectedMetric: AnalyzeMetric = AnalyzeMetric.TOP_SELLING,
    val topSellingProducts: List<ProductWithSummary> = emptyList(),
    val topProfitableProducts: List<ProductWithSummary> = emptyList(),
    /** A fetch is running with nothing to show yet — full-screen loading state. */
    val isLoading: Boolean = false,
    /** A fetch is running while the previous data stays visible — inline progress state. */
    val isRefreshing: Boolean = false,
    val error: String? = null
) {
    /** Dataset currently rendered on the chart. */
    val activeProducts: List<ProductWithSummary>
        get() = if (selectedMetric == AnalyzeMetric.TOP_SELLING) topSellingProducts else topProfitableProducts

    val isEmpty: Boolean get() = activeProducts.isEmpty()
    val productLabels: List<String> get() = activeProducts.map { it.product.name.value.toChartLabel() }
}

private fun String.toChartLabel(): String =
    if (length > MAX_LABEL_LENGTH) "${take(MAX_LABEL_LENGTH - 1)}…" else this

private const val MAX_LABEL_LENGTH = 10
