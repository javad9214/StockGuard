package ir.yar.anbar.ui.viewmodels.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.model.type.Money
import ir.yar.anbar.domain.usecase.analytics.GetInvoiceReportCountUseCase
import ir.yar.anbar.domain.usecase.analytics.GetLowStockProductsUseCase
import ir.yar.anbar.domain.usecase.analytics.GetTotalProfitPriceUseCase
import ir.yar.anbar.domain.usecase.analytics.GetTotalSoldPriceUseCase
import ir.yar.anbar.domain.usecase.sales.GetTopProfitableProductsUseCase
import ir.yar.anbar.domain.usecase.sales.GetTopSellingProductsUseCase
import ir.yar.anbar.domain.usecase.userpreferences.GetStockRunoutLimitUseCase
import ir.yar.anbar.utils.dateandtime.TimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// Data classes for combined UI state
data class ProductWithSummary(
    val product: Product,
    val summary: ProductSalesSummary,
    val rank: Int
)

data class HomeAnalyticsState(
    val totalInvoiceCount: Int = 0,
    val totalSales: Money = Money(0),
    val totalProfit: Money = Money(0)
)

data class HomeProductsState(
    val topSellingProducts: List<ProductWithSummary> = emptyList(),
    val topProfitableProducts: List<ProductWithSummary> = emptyList(),
    val lowStockProducts: List<Product> = emptyList()
)

data class HomeScreenState(
    val analytics: HomeAnalyticsState = HomeAnalyticsState(),
    val products: HomeProductsState = HomeProductsState(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeTotalItemsViewModel @Inject constructor(
    private val getInvoiceReportCountUseCase: GetInvoiceReportCountUseCase,
    private val getTotalSoldPriceUseCase: GetTotalSoldPriceUseCase,
    private val getTotalProfitPriceUseCase: GetTotalProfitPriceUseCase,
    private val getTopSellingProductsUseCase: GetTopSellingProductsUseCase,
    private val getTopProfitableProductsUseCase: GetTopProfitableProductsUseCase,
    private val getLowStockProductsUseCase: GetLowStockProductsUseCase,
    private val getStockRunoutLimitUseCase: GetStockRunoutLimitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    private val _stockRunoutLimit = MutableStateFlow(0)
    val stockRunoutLimit: StateFlow<Int> = _stockRunoutLimit.asStateFlow()

    init {
        loadAnalyticsData(TimeRange.TODAY)
        loadProductSalesSummary(TimeRange.TODAY)
        loadStockLimit()
    }

    private fun loadAnalyticsData(timeRange: TimeRange) {
        viewModelScope.launch {
            setLoading(true)
            try {
                // Combine all analytics flows
                combine(
                    getInvoiceReportCountUseCase.invoke(timeRange),
                    getTotalSoldPriceUseCase.invoke(timeRange),
                    getTotalProfitPriceUseCase.invoke(timeRange)
                ) { invoiceCount, soldPrice, profitPrice ->
                    HomeAnalyticsState(
                        totalInvoiceCount = invoiceCount,
                        totalSales = Money(soldPrice),
                        totalProfit = Money(profitPrice)
                    )
                }.collect { analyticsState ->
                    _uiState.value = _uiState.value.copy(analytics = analyticsState)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun loadProductSalesSummary(timeRange: TimeRange) {
        viewModelScope.launch {
            setLoading(true)
            try {
                // Launch both separately but update state atomically
                val topSellingJob = launch {
                    getTopSellingProductsUseCase.invoke(timeRange)
                        .collect { (summaryList, products) ->
                            val combined = combineProductsWithSummary(summaryList, products)
                            Log.i(TAG, "Top selling products: ${combined.size}")

                            _uiState.value = _uiState.value.copy(
                                products = _uiState.value.products.copy(
                                    topSellingProducts = combined
                                )
                            )
                        }
                }

                val topProfitableJob = launch {
                    getTopProfitableProductsUseCase.invoke(timeRange)
                        .collect { (summaryList, products) ->
                            val combined = combineProductsWithSummary(summaryList, products)
                            Log.d(TAG, "Top profitable products: ${combined.size}")

                            _uiState.value = _uiState.value.copy(
                                products = _uiState.value.products.copy(
                                    topProfitableProducts = combined
                                )
                            )
                        }
                }

                // Wait for both to complete
                topSellingJob.join()
                topProfitableJob.join()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun loadStockLimit() {
        viewModelScope.launch {
            getStockRunoutLimitUseCase.invoke().collect { stockLimit ->
                _stockRunoutLimit.value = stockLimit
                loadLowStockProducts(stockLimit)
            }
        }
    }

    private fun loadLowStockProducts(stockLimit: Int) {
        viewModelScope.launch {
            setLoading(true)
            try {
                getLowStockProductsUseCase.invoke(stockLimit).collect { products ->
                    _uiState.value = _uiState.value.copy(
                        products = _uiState.value.products.copy(
                            lowStockProducts = products
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    fun reLoadProductSaleSummary(timeRange: TimeRange) {
        loadProductSalesSummary(timeRange)
        loadAnalyticsData(timeRange)
    }

    private fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    // Helper function to combine products with their summaries efficiently
    private fun combineProductsWithSummary(
        summaryList: List<ProductSalesSummary>,
        products: List<Product>
    ): List<ProductWithSummary> {
        // Create a map for O(1) lookup instead of O(n) find()
        val productMap = products.associateBy { it.id }

        return summaryList.mapIndexedNotNull { index, summary ->
            productMap[summary.productId]?.let { product ->
                ProductWithSummary(
                    product = product,
                    summary = summary,
                    rank = index + 1
                )
            }
        }
    }

    companion object {
        const val TAG = "HomeTotalItemsViewModel"
    }
}