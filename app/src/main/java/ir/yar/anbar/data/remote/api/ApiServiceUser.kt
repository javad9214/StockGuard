package ir.yar.anbar.data.remote.api

import com.skydoves.sandwich.ApiResponse
import ir.yar.anbar.data.remote.dto.response.UserResponseDto
import retrofit2.http.GET

/**
 * API Service for user account endpoints
 */
interface ApiServiceUser {

    /**
     * Get the profile of the currently authenticated user
     * GET /api/users/profile
     */
    @GET(ApiConstants.API + ApiConstants.AUTH + "profile")
    suspend fun getUserProfile(): ApiResponse<UserResponseDto>
}
