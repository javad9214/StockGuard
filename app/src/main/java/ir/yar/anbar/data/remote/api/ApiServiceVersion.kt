package ir.yar.anbar.data.remote.api


import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.dto.response.AppVersionResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API Service for app version checking
 */
interface ApiServiceVersion {

    /**
     * Get version configuration for Android (PUBLIC - No auth required)
     * GET /api/version/android
     */
    @GET(ApiConstants.API + "version/android")
    suspend fun getAndroidVersion(): ApiResponse<AppVersionResponseDto>

    /**
     * Get version configuration for a specific platform (PUBLIC - No auth required)
     * GET /api/version/{platform}
     *
     * @param platform "android" or "ios"
     * @return AppVersionResponseDto with version information
     */
    @GET(ApiConstants.API + "version/{platform}")
    suspend fun getVersionByPlatform(
        @Path("platform") platform: String
    ): ApiResponse<AppVersionResponseDto>

    /**
     * Get all version configurations
     * GET /api/version/admin/all
     */
    @GET(ApiConstants.API + "version/admin/all")
    suspend fun getAllVersions(): ApiResponse<List<AppVersionResponseDto>>

    companion object {
        const val PLATFORM_ANDROID = "android"  // lowercase for public API
        const val PLATFORM_IOS = "ios"
    }
}