package ir.yar.anbar.domain.usecase.versionupdate



import ir.yar.anbar.data.remote.dto.response.AppVersionInfo
import ir.yar.anbar.data.remote.dto.response.UpdateStatus
import ir.yar.anbar.domain.repository.VersionRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckAppVersionUseCase @Inject constructor(
    private val repository: VersionRepository
) {

    /**
     * Check if an update is available for the current app version
     *
     * @param currentVersionCode Current version code of the app
     */
    suspend operator fun invoke(
        currentVersionCode: Int
    ): Flow<Resource<Pair<AppVersionInfo, UpdateStatus>>> {
        return repository.checkForUpdates(currentVersionCode)
    }
}

/**
 * Data class to hold version check result
 */
data class VersionCheckResult(
    val versionInfo: AppVersionInfo,
    val updateStatus: UpdateStatus,
    val currentVersionCode: Int
) {
    val isUpdateRequired: Boolean
        get() = updateStatus == UpdateStatus.UPDATE_REQUIRED

    val isUpdateAvailable: Boolean
        get() = updateStatus == UpdateStatus.UPDATE_AVAILABLE

    val shouldShowDialog: Boolean
        get() = isUpdateRequired || isUpdateAvailable
}

