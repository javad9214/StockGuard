package ir.yar.anbar.data.remote.api


import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.dto.response.AppVersionResponseDto
import retrofit2.http.GET

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
}
