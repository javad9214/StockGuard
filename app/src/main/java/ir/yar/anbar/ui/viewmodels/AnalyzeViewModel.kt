package ir.yar.anbar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.yar.anbar.ui.viewmodels.home.HomeScreenState
import ir.yar.anbar.ui.viewmodels.home.ProductWithSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Dedicated ViewModel for the Analyze screen.
 *
 * It fetches nothing itself: it wraps the state exposed by
 * [ir.yar.anbar.ui.viewmodels.home.HomeTotalItemsViewModel] (forwarded by the screen)
 * and transforms it into the format required by Vico (chart entries + axis labels),
 * so no data-loading logic is duplicated.
 */
@HiltViewModel
class AnalyzeViewModel @Inject constructor() : ViewModel() {

    /** Latest state exposed by HomeTotalItemsViewModel, forwarded via [onHomeStateChanged]. */
    private val homeScreenState = MutableStateFlow(HomeScreenState())

    /** Owns the chart data so the screen stays UI-only. */
    val modelProducer = CartesianChartModelProducer()

    val uiState: StateFlow<AnalyzeUiState> = homeScreenState
        .map(::toAnalyzeUiState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AnalyzeUiState())

    init {
        viewModelScope.launch {
            homeScreenState
                .map { it.products.topSellingProducts }
                .distinctUntilChanged()
                .collect { topSelling -> updateChart(topSelling) }
        }
    }

    /** Called by the Analyze screen with the state exposed by HomeTotalItemsViewModel. */
    fun onHomeStateChanged(state: HomeScreenState) {
        homeScreenState.value = state
    }

    private fun toAnalyzeUiState(state: HomeScreenState): AnalyzeUiState = AnalyzeUiState(
        productLabels = state.products.topSellingProducts.map { it.product.name.value.toChartLabel() },
        isLoading = state.isLoading,
        isEmpty = state.products.topSellingProducts.isEmpty(),
        error = state.errorMessage
    )

    /** Maps top-selling products into Vico entries (revenue per product, in display units). */
    private suspend fun updateChart(topSellingProducts: List<ProductWithSummary>) {
        if (topSellingProducts.isEmpty()) return

        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                columnSeries {
                    series(
                        x = topSellingProducts.map { it.rank - 1 },
                        y = topSellingProducts.map { it.summary.totalRevenue.toDisplayAmount() }
                    )
                }
            }
        }
    }

    private fun String.toChartLabel(): String =
        if (length > MAX_LABEL_LENGTH) "${take(MAX_LABEL_LENGTH - 1)}…" else this

    companion object {
        private const val MAX_LABEL_LENGTH = 10
    }
}

data class AnalyzeUiState(
    val productLabels: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = true,
    val error: String? = null
)
