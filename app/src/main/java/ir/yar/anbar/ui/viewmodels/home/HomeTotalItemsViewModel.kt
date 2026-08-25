package ir.yar.anbar.ui.viewmodels.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.model.type.Money
import ir.yar.anbar.domain.repository.UserPreferencesRepository
import ir.yar.anbar.domain.usecase.analytics.GetInvoiceReportCountUseCase
import ir.yar.anbar.domain.usecase.analytics.GetLowStockProductsUseCase
import ir.yar.anbar.domain.usecase.analytics.GetTotalProfitPriceUseCase
import ir.yar.anbar.domain.usecase.analytics.GetTotalSoldPriceUseCase
import ir.yar.anbar.domain.usecase.sales.GetTopProfitableProductsUseCase
import ir.yar.anbar.domain.usecase.sales.GetTopSellingProductsUseCase
import ir.yar.anbar.domain.usecase.userpreferences.GetStockRunoutLimitUseCase
import ir.yar.anbar.utils.dateandtime.TimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
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

    private val _stockRunoutLimit =
        MutableStateFlow(UserPreferencesRepository.DEFAULT_STOCK_RUNOUT_LIMIT)
    val stockRunoutLimit: StateFlow<Int> = _stockRunoutLimit.asStateFlow()

    // One job per section. Each loader cancels its predecessor, so collectors for
    // a previous period can never overwrite a newer period's data on DB changes.
    private var analyticsJob: Job? = null
    private var productsJob: Job? = null
    private var lowStockJob: Job? = null

    init {
        loadAnalyticsData(TimeRange.TODAY)
        loadProductSalesSummary(TimeRange.TODAY)
        loadStockLimit()
    }

    private fun loadAnalyticsData(timeRange: TimeRange) {
        analyticsJob?.cancel()
        analyticsJob = viewModelScope.launch {
            clearError()
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
                    // Room flows never complete, so loading must stop on the first
                    // emission; nothing else terminates this collector
                    _uiState.value = _uiState.value.copy(
                        analytics = analyticsState,
                        isLoading = false
                    )
                }
            } catch (e: CancellationException) {
                // Cancellation means this load was replaced by a newer one —
                // and swallowing it would break structured concurrency
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error",
                    isLoading = false
                )
            } finally {
                // Skip when cancelled: the replacement load owns the flag now
                if (isActive) setLoading(false)
            }
        }
    }

    private fun loadProductSalesSummary(timeRange: TimeRange) {
        productsJob?.cancel()
        productsJob = viewModelScope.launch {
            clearError()
            setLoading(true)
            // Room flows never complete, so loading stops once both collectors
            // have delivered their first emission
            var topSellingLoaded = false
            var topProfitableLoaded = false
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

                            topSellingLoaded = true
                            if (topProfitableLoaded) setLoading(false)
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

                            topProfitableLoaded = true
                            if (topSellingLoaded) setLoading(false)
                        }
                }

                // Wait for both to complete
                topSellingJob.join()
                topProfitableJob.join()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error",
                    isLoading = false
                )
            } finally {
                if (isActive) setLoading(false)
            }
        }
    }

    private fun loadStockLimit() {
        viewModelScope.launch {
            getStockRunoutLimitUseCase.invoke().collect { stockLimit ->
                _stockRunoutLimit.value = stockLimit
                // loadLowStockProducts cancels its predecessor, so a changed
                // limit replaces the collector instead of stacking another
                loadLowStockProducts(stockLimit)
            }
        }
    }

    private fun loadLowStockProducts(stockLimit: Int) {
        lowStockJob?.cancel()
        lowStockJob = viewModelScope.launch {
            clearError()
            setLoading(true)
            try {
                getLowStockProductsUseCase.invoke(stockLimit).collect { products ->
                    _uiState.value = _uiState.value.copy(
                        products = _uiState.value.products.copy(
                            lowStockProducts = products
                        ),
                        isLoading = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error",
                    isLoading = false
                )
            } finally {
                if (isActive) setLoading(false)
            }
        }
    }

    /**
     * Reloads the period-dependent sections (products and analytics) for the
     * given range — used by the date picker, error retry and pull-to-refresh.
     * Each loader cancels the previous period's collectors first.
     */
    fun reLoadProductSaleSummary(timeRange: TimeRange) {
        loadProductSalesSummary(timeRange)
        loadAnalyticsData(timeRange)
    }

    private fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    // Drop any previous error so a fresh load starts from a clean state;
    // a failure during the load sets a new message
    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
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
