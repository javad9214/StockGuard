package ir.yar.anbar.ui.viewmodels.versionupdate

import ir.yar.anbar.data.remote.dto.response.AppVersionInfo
import ir.yar.anbar.data.remote.dto.response.UpdateStatus


/**
 * UI State for version checking
 */
data class VersionUiState(
    val isLoading: Boolean = false,
    val versionInfo: AppVersionInfo? = null,
    val updateStatus: UpdateStatus = UpdateStatus.UP_TO_DATE,
    val errorMessage: String? = null,
    val showUpdateDialog: Boolean = false,
    val currentVersionCode: Int = 0,
    val currentVersionName: String = ""
) {
    /**
     * Check if update is required (force update)
     */
    val isUpdateRequired: Boolean
        get() = updateStatus == UpdateStatus.UPDATE_REQUIRED

    /**
     * Check if update is available (optional)
     */
    val isUpdateAvailable: Boolean
        get() = updateStatus == UpdateStatus.UPDATE_AVAILABLE

    /**
     * Check if user can skip the update
     */
    val canSkipUpdate: Boolean
        get() = !isUpdateRequired && isUpdateAvailable

    /**
     * Get the dialog title based on update type
     */
    val dialogTitle: String
        get() = when {
            isUpdateRequired -> "Update Required"
            isUpdateAvailable -> "Update Available"
            else -> "Check for Updates"
        }

    /**
     * Get the dialog message
     */
    val dialogMessage: String
        get() = when {
            isUpdateRequired -> "A new version is required to continue using the app. Please update now."
            isUpdateAvailable -> "A new version is available with improvements and bug fixes."
            else -> ""
        }
}

/**
 * Events that can be triggered from the ViewModel
 */
sealed class VersionEvent {
    data object UpdateCheckStarted : VersionEvent()
    data object UpdateCheckCompleted : VersionEvent()
    data class  UpdateCheckFailed(val error: String) : VersionEvent()
    data object DialogShown : VersionEvent()
    data object DialogDismissed : VersionEvent()
    data object UpdateClicked : VersionEvent()
    data object SkipClicked : VersionEvent()
}