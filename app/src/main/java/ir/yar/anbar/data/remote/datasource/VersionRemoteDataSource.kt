package ir.yar.anbar.data.remote.datasource


import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.api.ApiServiceVersion
import ir.yar.anbar.data.remote.dto.response.AppVersionResponseDto
import javax.inject.Inject

/**
 * Remote data source for app version operations
 */
class VersionRemoteDataSource @Inject constructor(
    private val apiService: ApiServiceVersion
) {

    /**
     * Fetch version information for Android platform (PUBLIC endpoint)
     */
    suspend fun getAndroidVersion(): ApiResponse<AppVersionResponseDto> {
        return apiService.getAndroidVersion()
    }

    /**
     * Fetch version information for a specific platform
     */
    suspend fun getVersionByPlatform(platform: String): ApiResponse<AppVersionResponseDto> {
        return apiService.getVersionByPlatform(platform)
    }

    /**
     * Fetch all version configurations
     */
    suspend fun getAllVersions(): ApiResponse<List<AppVersionResponseDto>> {
        return apiService.getAllVersions()
    }
}