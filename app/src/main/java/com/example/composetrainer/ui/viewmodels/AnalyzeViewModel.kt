package com.example.composetrainer.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.AnalyticsData
import com.example.composetrainer.domain.model.ProductSalesSummary
import com.example.composetrainer.utils.dateandtime.TimeRange
import com.example.composetrainer.domain.usecase.analytics.GetAnalyticsDataUseCase
import com.example.composetrainer.domain.usecase.analytics.GetInvoiceReportCountUseCase
import com.example.composetrainer.domain.usecase.sales.GetProductSalesSummaryUseCase
import com.example.composetrainer.ui.screens.InvoiceSummaryRange
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
    private val getProductSalesSummaryUseCase: GetProductSalesSummaryUseCase,
    private val getInvoiceReportCountUseCase: GetInvoiceReportCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyzeUiState())
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
        loadProductSalesSummary(TimeRange.THIS_MONTH) // Default to this month
        loadInvoiceCountForSummaryRange(InvoiceSummaryRange.THIS_MONTH)
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

    fun loadProductSalesSummary(timeRange: TimeRange) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedTimeRange = timeRange) }
            try {
                val productSalesSummary = getProductSalesSummaryUseCase(timeRange)
                _uiState.update {
                    it.copy(
                        productSalesSummary = productSalesSummary,
                        isLoading = false,
                        error = null
                    )
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

    fun loadInvoiceCountForSummaryRange(range: InvoiceSummaryRange) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val count = when (range) {
                    InvoiceSummaryRange.TODAY -> getInvoiceReportCountUseCase.getTodayInvoiceCount()
                    InvoiceSummaryRange.YESTERDAY -> getInvoiceReportCountUseCase.getYesterdayInvoiceCount()
                    InvoiceSummaryRange.THIS_WEEK -> getInvoiceReportCountUseCase.getThisWeekInvoiceCount()
                    InvoiceSummaryRange.LAST_WEEK -> getInvoiceReportCountUseCase.getLastWeekInvoiceCount()
                    InvoiceSummaryRange.THIS_MONTH -> getInvoiceReportCountUseCase.getCurrentMonthInvoiceCount()
                    InvoiceSummaryRange.LAST_MONTH -> getInvoiceReportCountUseCase.getLastMonthInvoiceCount()
                }
                _uiState.update { it.copy(invoiceCount = count, isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
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
    val selectedTimeRange: TimeRange = TimeRange.THIS_MONTH,
    val isLoading: Boolean = false,
    val error: String? = null,
    val invoiceCount: Int = 0
)