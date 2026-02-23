package ir.yar.login.data.remote.api

import ir.yar.login.data.remote.dto.request.LoginRequest
import ir.yar.login.data.remote.dto.request.RefreshTokenRequest
import ir.yar.login.data.remote.dto.request.RegisterRequest
import ir.yar.login.data.remote.dto.response.AuthResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiAuthService {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponseDTO>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponseDTO>

    @POST("api/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<AuthResponseDTO>
}