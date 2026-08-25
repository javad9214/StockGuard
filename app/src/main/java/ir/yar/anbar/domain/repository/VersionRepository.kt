package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.AppVersionInfo
import ir.yar.anbar.domain.model.UpdateStatus
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app version management
 */
interface VersionRepository {

    /**
     * Check for app updates for Android platform
     * @param currentVersionCode Current version code of the app
     * @return Flow of Resource containing AppVersionInfo and UpdateStatus
     */
    fun checkForUpdates(currentVersionCode: Int): Flow<Resource<Pair<AppVersionInfo, UpdateStatus>>>
}
