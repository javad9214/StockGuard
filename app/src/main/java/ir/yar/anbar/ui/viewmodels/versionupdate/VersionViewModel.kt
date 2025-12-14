package ir.yar.anbar.ui.viewmodels.versionupdate


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.yar.anbar.data.remote.dto.response.UpdateStatus
import ir.yar.anbar.domain.usecase.versionupdate.CheckAppVersionUseCase
import ir.yar.anbar.utils.VersionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing app version checking and updates
 */
@HiltViewModel
class VersionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val checkAppVersionUseCase: CheckAppVersionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VersionUiState())
    val uiState: StateFlow<VersionUiState> = _uiState.asStateFlow()


    init {
        // Get current version info
        val versionCode = VersionUtils.getVersionCode(context)
        val versionName = VersionUtils.getVersionName(context)

        _uiState.update {
            it.copy(
                currentVersionCode = versionCode,
                currentVersionName = versionName
            )
        }
    }

    /**
     * Check for app updates
     * @param showDialogOnUpdate If true, shows dialog when update is available
     */
    fun checkForUpdates(showDialogOnUpdate: Boolean = true) {
        Log.i(TAG, "checkForUpdates: called")
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            Log.i(TAG, "checkForUpdates: currentVersionCode: ${_uiState.value.currentVersionCode}")

            checkAppVersionUseCase(
                currentVersionCode = _uiState.value.currentVersionCode
            ).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        val (versionInfo, updateStatus) = result.data

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                versionInfo = versionInfo,
                                updateStatus = updateStatus,
                                showUpdateDialog = showDialogOnUpdate &&
                                        (updateStatus == UpdateStatus.UPDATE_REQUIRED ||
                                                updateStatus == UpdateStatus.UPDATE_AVAILABLE),
                                errorMessage = null
                            )
                        }

                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Silently check for updates without showing dialog
     * Useful for background checks
     */
    fun silentUpdateCheck() {
        checkForUpdates(showDialogOnUpdate = false)
    }

    /**
     * Show the update dialog manually
     */
    fun showUpdateDialog() {
        _uiState.update { it.copy(showUpdateDialog = true) }

    }

    /**
     * Dismiss the update dialog
     * Only allowed if update is not required
     */
    fun dismissUpdateDialog() {
        if (!_uiState.value.isUpdateRequired) {
            _uiState.update { it.copy(showUpdateDialog = false) }

        }
    }

    /**
     * User clicked the update button
     */
    fun onUpdateClicked() {
        Log.i(TAG, "onUpdateClicked: User clicked update")
    }

    /**
     * User clicked skip update (only for optional updates)
     */
    fun onSkipUpdate() {
        if (_uiState.value.canSkipUpdate) {
            dismissUpdateDialog()

        }
    }

    /**
     * Reset error state
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Get the update URL from version info
     */
    fun getUpdateUrl(): String? {
        return _uiState.value.versionInfo?.updateUrl
    }

    /**
     * Check if an update check is needed
     * Can be used to implement logic like "check once per day"
     */
    fun shouldCheckForUpdates(): Boolean {
        // TODO: Implement logic based on last check time
        // For now, always return true
        return true
    }

    companion object{
        const val TAG = "UpdateViewModel"
    }
}