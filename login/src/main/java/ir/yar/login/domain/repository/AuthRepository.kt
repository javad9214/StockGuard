package ir.yar.login.domain.repository

import ir.yar.login.data.remote.dto.request.LoginRequest
import ir.yar.login.data.remote.dto.request.RegisterRequest
import ir.yar.login.domain.model.AuthResult
import ir.yar.login.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(request: RegisterRequest): Flow<Result<ir.yar.login.domain.model.AuthResult>>
    suspend fun login(request: LoginRequest): Flow<Result<ir.yar.login.domain.model.AuthResult>>
    suspend fun refreshToken(): Flow<Result<ir.yar.login.domain.model.AuthResult>>
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
    suspend fun isLoggedIn(): Boolean
}