package ir.yar.anbar.domain.usecase.versionupdate

import ir.yar.anbar.domain.model.AppVersionInfo
import ir.yar.anbar.domain.model.UpdateStatus
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
    operator fun invoke(
        currentVersionCode: Int
    ): Flow<Resource<Pair<AppVersionInfo, UpdateStatus>>> {
        return repository.checkForUpdates(currentVersionCode)
    }
}
