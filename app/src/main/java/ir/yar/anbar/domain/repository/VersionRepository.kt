package ir.yar.anbar.domain.repository


import ir.yar.anbar.data.remote.dto.response.AppVersionInfo
import ir.yar.anbar.data.remote.dto.response.UpdateStatus
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
    suspend fun checkForUpdates(currentVersionCode: Int): Flow<Resource<Pair<AppVersionInfo, UpdateStatus>>>

    /**
     * Get version information for a specific platform
     * @param platform Platform name (ANDROID or IOS)
     * @return Flow of Resource containing AppVersionInfo
     */
    suspend fun getVersionByPlatform(platform: String): Flow<Resource<AppVersionInfo>>

    /**
     * Get all version configurations
     * @return Flow of Resource containing list of AppVersionInfo
     */
    suspend fun getAllVersions(): Flow<Resource<List<AppVersionInfo>>>
}