package com.example.composetrainer.ui.viewmodels.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.model.ProductSalesSummary
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.domain.usecase.analytics.GetInvoiceReportCountUseCase
import com.example.composetrainer.domain.usecase.analytics.GetTotalProfitPriceUseCase
import com.example.composetrainer.domain.usecase.analytics.GetTotalSoldPriceUseCase
import com.example.composetrainer.domain.usecase.sales.GetProductSalesSummaryUseCase
import com.example.composetrainer.utils.dateandtime.TimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeTotalItemsViewModel @Inject constructor(
    private val getInvoiceReportCountUseCase: GetInvoiceReportCountUseCase,
    private val getTotalSoldPriceUseCase: GetTotalSoldPriceUseCase,
    private val getTotalProfitPriceUseCase: GetTotalProfitPriceUseCase,
    private val getProductSalesSummaryUseCase: GetProductSalesSummaryUseCase
) : ViewModel() {

    private val _totalInvoiceCount = MutableStateFlow(0)
    val totalInvoiceCount: StateFlow<Int> get() = _totalInvoiceCount

    private val _totalSoldPrice = MutableStateFlow(Money(0))
    val totalSoldPrice: StateFlow<Money> get() = _totalSoldPrice

    private val _totalProfitPrice = MutableStateFlow(Money(0))
    val totalProfitPrice: StateFlow<Money> get() = _totalProfitPrice

    private val _productSalesSummaryList = MutableStateFlow<List<ProductSalesSummary>>(emptyList())
    val productSalesSummaryList: StateFlow<List<ProductSalesSummary>> get() = _productSalesSummaryList

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    init {
        loadAnalyticsData()
        loadProductSalesSummary(TimeRange.TODAY)
    }

    private fun loadAnalyticsData(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _totalInvoiceCount.value = getInvoiceReportCountUseCase.getTodayInvoiceCount()
                _totalSoldPrice.value = getTotalSoldPriceUseCase.getTodayTotalSold()
                _totalProfitPrice.value = getTotalProfitPriceUseCase.getTodayTotalProfit()
                _isLoading.value = false

            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    private fun loadProductSalesSummary(timeRange: TimeRange){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (productsSaleSummeryList, products) = getProductSalesSummaryUseCase.invoke(timeRange)
                _productSalesSummaryList.value = productsSaleSummeryList
                _products.value = products
                _isLoading.value = false

            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun reLoadProductSaleSummary(timeRange: TimeRange){
        loadProductSalesSummary(timeRange)
    }

    companion object {
        const val TAG = "HomeTotalItemsViewModel"
    }
}