package ir.yar.anbar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.domain.usecase.userpreferences.GetStockRunoutLimitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.SaveStockRunoutLimitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getStockRunoutLimitUseCase: GetStockRunoutLimitUseCase,
    private val saveStockRunoutLimitUseCase: SaveStockRunoutLimitUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _stockRunoutLimit = MutableStateFlow(5)
    val stockRunoutLimit: StateFlow<Int> = _stockRunoutLimit

    init {
        observeStockRunoutLimit()
    }

    private fun observeStockRunoutLimit() {
        viewModelScope.launch {
            getStockRunoutLimitUseCase().collectLatest { limit ->
                _stockRunoutLimit.value = limit
            }
        }
    }

    fun saveStockRunoutLimit(limit: Int) {
        viewModelScope.launch {
            saveStockRunoutLimitUseCase(limit)
        }
    }


}