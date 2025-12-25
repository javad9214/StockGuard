package ir.yar.anbar.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.yar.anbar.domain.model.AnalyticsData
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.model.analyze.DailySalesData
import ir.yar.anbar.domain.usecase.analytics.GetAnalyticsDataUseCase
import ir.yar.anbar.domain.usecase.analytics.GetDailySalesBreakdownUseCase
import ir.yar.anbar.domain.usecase.sales.GetTopSellingProductsUseCase
import ir.yar.anbar.utils.dateandtime.TimeRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyzeViewModel @Inject constructor(
    private val getAnalyticsDataUseCase: GetAnalyticsDataUseCase,
    private val getTopSellingProductsUseCase: GetTopSellingProductsUseCase,
    private val getDailySalesBreakdownUseCase: GetDailySalesBreakdownUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyzeUiState())
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        loadAnalyticsData()
        loadProductSalesSummary(TimeRange.THIS_MONTH)
        loadDailySalesData()
    }

    private fun loadAnalyticsData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                Log.d(TAG, "Loading analytics data...")
                val analyticsData = getAnalyticsDataUseCase()
                Log.d(TAG, "Analytics data loaded: $analyticsData")
                _uiState.update {
                    it.copy(
                        analyticsData = analyticsData,
                        error = null
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading analytics data", e)
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Unknown error"
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadProductSalesSummary(timeRange: TimeRange) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedTimeRange = timeRange) }
            try {
                getTopSellingProductsUseCase(timeRange).collect { (summaries, products) ->
                    _uiState.update {
                        it.copy(
                            productSalesSummary = summaries,
                            products = products,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading product sales summary", e)
                _uiState.update {
                    it.copy(error = e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun loadDailySalesData() {
        viewModelScope.launch {
            try {
                // Get real daily breakdown for last 7 days
                getDailySalesBreakdownUseCase(TimeRange.THIS_WEEK).collect { dailySales ->
                    Log.d(TAG, "Daily sales data loaded: ${dailySales.size} days")
                    _uiState.update {
                        it.copy(
                            dailySalesChartData = dailySales,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading daily sales data", e)
                _uiState.update {
                    it.copy(error = e.message ?: "Unknown error")
                }
            }
        }
    }

    fun onTimeRangeChanged(timeRange: TimeRange) {
        loadProductSalesSummary(timeRange)
        loadDailySalesData()
    }

    fun refresh() {
        loadAllData()
    }

    companion object {
        private const val TAG = "AnalyzeViewModel"
    }
}

data class AnalyzeUiState(
    val analyticsData: AnalyticsData? = null,
    val productSalesSummary: List<ProductSalesSummary> = emptyList(),
    val products: List<Product> = emptyList(),
    val dailySalesChartData: List<DailySalesData> = emptyList(),
    val selectedTimeRange: TimeRange = TimeRange.THIS_MONTH,
    val isLoading: Boolean = false,
    val error: String? = null
)