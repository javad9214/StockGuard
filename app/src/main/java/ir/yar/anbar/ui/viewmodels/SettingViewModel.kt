package ir.yar.anbar.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.domain.model.UnitOfMeasure
import ir.yar.anbar.domain.repository.UserPreferencesRepository
import ir.yar.anbar.domain.usecase.userpreferences.GetDefaultUnitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.GetStockRunoutLimitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.GetVisibleUnitsUseCase
import ir.yar.anbar.domain.usecase.userpreferences.SaveDefaultUnitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.SaveStockRunoutLimitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.SaveVisibleUnitsUseCase
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
    // Exact UnitOfMeasure enum name, same seeding rationale as above
    val defaultUnit: String = UserPreferencesRepository.DEFAULT_UNIT,
    // Enum names offered in the unit pickers
    val visibleUnits: Set<String> = UserPreferencesRepository.DEFAULT_VISIBLE_UNITS,
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
    private val saveStockRunoutLimitUseCase: SaveStockRunoutLimitUseCase,
    private val getDefaultUnitUseCase: GetDefaultUnitUseCase,
    private val saveDefaultUnitUseCase: SaveDefaultUnitUseCase,
    private val getVisibleUnitsUseCase: GetVisibleUnitsUseCase,
    private val saveVisibleUnitsUseCase: SaveVisibleUnitsUseCase
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
        observeDefaultUnit()
        observeVisibleUnits()
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

    private fun observeDefaultUnit() {
        viewModelScope.launch {
            try {
                getDefaultUnitUseCase().collectLatest { unit ->
                    _uiState.value = _uiState.value.copy(defaultUnit = unit)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to observe default unit", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    // A dropdown emits one discrete selection, so — unlike the slider — no
    // debounce is needed; just write through
    fun saveDefaultUnit(unit: String) {
        if (UnitOfMeasure.fromName(unit) == null) {
            // Unreachable from the selector, which only offers enum values —
            // a rejection means a programmatic caller is out of sync
            Log.w(TAG, "Rejected unknown default unit: $unit")
            return
        }

        _uiState.value = _uiState.value.copy(defaultUnit = unit)
        viewModelScope.launch {
            try {
                saveDefaultUnitUseCase(unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save default unit: $unit", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    private fun observeVisibleUnits() {
        viewModelScope.launch {
            try {
                getVisibleUnitsUseCase().collectLatest { units ->
                    _uiState.value = _uiState.value.copy(visibleUnits = units)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to observe visible units", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun saveVisibleUnits(units: Set<String>) {
        // Unknown names are dropped so a stale value can never reach the
        // pickers; an empty result is rejected outright — hiding every unit
        // would leave the dropdowns with nothing to offer
        val valid = units.mapNotNull { UnitOfMeasure.fromName(it)?.name }.toSet()
        if (valid.isEmpty()) {
            // Unreachable from the dialog, whose confirm button disables on
            // an empty selection — a rejection means a caller is out of sync
            Log.w(TAG, "Rejected empty visible units set")
            return
        }

        _uiState.value = _uiState.value.copy(visibleUnits = valid)
        viewModelScope.launch {
            try {
                saveVisibleUnitsUseCase(valid)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save visible units", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
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
