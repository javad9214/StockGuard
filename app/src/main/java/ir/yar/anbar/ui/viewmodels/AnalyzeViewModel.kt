package ir.yar.anbar.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.domain.model.AnalyticsData
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.utils.dateandtime.TimeRange
import ir.yar.anbar.domain.usecase.analytics.GetAnalyticsDataUseCase
import ir.yar.anbar.domain.usecase.analytics.GetInvoiceReportCountUseCase
import ir.yar.anbar.domain.usecase.sales.GetTopSellingProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val getInvoiceReportCountUseCase: GetInvoiceReportCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyzeUiState())
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
        loadProductSalesSummary(TimeRange.THIS_MONTH) // Default to this month
    }

    private fun loadAnalyticsData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                Log.d("AnalyzeViewModel", "Loading analytics data...")
                val analyticsData = getAnalyticsDataUseCase()
                Log.d("AnalyzeViewModel", "Analytics data loaded: $analyticsData")
                _uiState.update {
                    it.copy(
                        analyticsData = analyticsData,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                Log.e("AnalyzeViewModel", "Error loading analytics data", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun loadProductSalesSummary(timeRange: TimeRange) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedTimeRange = timeRange) }
            try {
                 getTopSellingProductsUseCase(timeRange).collect{ productSalesSummary->
                     _uiState.update {
                         it.copy(
                             productSalesSummary = productSalesSummary.first,
                             products = productSalesSummary.second,
                             isLoading = false,
                             error = null
                         )
                     }
                }

            } catch (e: Exception) {
                Log.e("AnalyzeViewModel", "Error loading product sales summary", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }


    fun refresh() {
        loadAnalyticsData()
        loadProductSalesSummary(uiState.value.selectedTimeRange)
    }
}

data class AnalyzeUiState(
    val analyticsData: AnalyticsData? = null,
    val productSalesSummary: List<ProductSalesSummary> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedTimeRange: TimeRange = TimeRange.THIS_MONTH,
    val isLoading: Boolean = false,
    val error: String? = null,
    val invoiceCount: Int = 0
)