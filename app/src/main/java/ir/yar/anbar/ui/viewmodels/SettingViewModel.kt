package ir.yar.anbar.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.domain.repository.UserPreferencesRepository
import ir.yar.anbar.domain.usecase.userpreferences.GetStockRunoutLimitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.SaveStockRunoutLimitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingUiState(
    // Seeded from the domain default so the pre-emission value matches what
    // the DataStore flow will deliver for a never-saved preference
    val stockRunoutLimit: Int = UserPreferencesRepository.DEFAULT_STOCK_RUNOUT_LIMIT,
    val errorMessage: String? = null
) {
    companion object {
        // Single source of truth for the preference's valid range — the
        // selector's slider range and the ViewModel's validation both derive
        // from these. 0 is valid: the low-stock query is `stock <= limit`,
        // so 0 means "only out-of-stock items"
        const val MIN_STOCK_RUNOUT_LIMIT = 0
        const val MAX_STOCK_RUNOUT_LIMIT = 50
    }
}

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getStockRunoutLimitUseCase: GetStockRunoutLimitUseCase,
    private val saveStockRunoutLimitUseCase: SaveStockRunoutLimitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    // The slider emits on every drag tick, so saves are funneled through a
    // debounced collector: only the last value within the window is written
    private val limitSaveRequests = MutableSharedFlow<Int>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // The DataStore flow echoes our own writes back; without this guard the
    // echo of a just-committed value could overwrite a newer optimistic one
    // if the user resumed dragging in between
    private var lastRequestedLimit: Int? = null

    init {
        observeStockRunoutLimit()
        observeLimitSaveRequests()
    }

    private fun observeStockRunoutLimit() {
        viewModelScope.launch {
            try {
                getStockRunoutLimitUseCase().collectLatest { limit ->
                    if (limit != lastRequestedLimit) {
                        _uiState.value = _uiState.value.copy(stockRunoutLimit = limit)
                    }
                }
            } catch (e: CancellationException) {
                // Cancellation is lifecycle-driven, not a failure — swallowing it
                // would break structured concurrency
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to observe stock runout limit", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeLimitSaveRequests() {
        viewModelScope.launch {
            limitSaveRequests
                .debounce(SAVE_DEBOUNCE_MILLIS)
                .collect { limit ->
                    try {
                        saveStockRunoutLimitUseCase(limit)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to save stock runout limit: $limit", e)
                        _uiState.value = _uiState.value.copy(
                            errorMessage = e.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    fun saveStockRunoutLimit(limit: Int) {
        // Reject before the optimistic update so an invalid value neither
        // reaches the UI state nor gets queued for persistence
        if (limit !in SettingUiState.MIN_STOCK_RUNOUT_LIMIT..SettingUiState.MAX_STOCK_RUNOUT_LIMIT) {
            // Unreachable from the slider, which shares these constants —
            // a rejection means a programmatic caller is out of sync
            Log.w(TAG, "Rejected out-of-range stock runout limit: $limit")
            _uiState.value = _uiState.value.copy(
                errorMessage = "Stock runout limit must be between " +
                        "${SettingUiState.MIN_STOCK_RUNOUT_LIMIT} and ${SettingUiState.MAX_STOCK_RUNOUT_LIMIT}"
            )
            return
        }

        // Optimistic update so the slider tracks the finger; the write itself
        // is debounced in observeLimitSaveRequests
        _uiState.value = _uiState.value.copy(stockRunoutLimit = limit)
        lastRequestedLimit = limit
        limitSaveRequests.tryEmit(limit)
    }

    // Called by the screen once the error snackbar has been shown, so the same
    // message isn't re-displayed on recomposition
    fun onErrorMessageShown() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    companion object {
        const val TAG = "SettingViewModel"
        private const val SAVE_DEBOUNCE_MILLIS = 300L
    }
}
