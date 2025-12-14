package ir.yar.login.data.repository


import ir.yar.login.data.remote.dto.request.LoginRequest
import ir.yar.login.data.remote.dto.request.RefreshTokenRequest
import ir.yar.login.data.remote.dto.request.RegisterRequest
import ir.yar.login.data.remote.dto.response.AuthResponseDTO
import ir.yar.login.domain.model.Result
import ir.yar.login.domain.model.User
import ir.yar.login.domain.repository.AuthRepository
import ir.yar.login.data.remote.api.ApiAuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiAuthService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun register(request: RegisterRequest): Flow<Result<ir.yar.login.domain.model.AuthResult>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.register(request)
            emit(handleAuthResponse(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun login(request: LoginRequest): Flow<Result<ir.yar.login.domain.model.AuthResult>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.login(request)
            emit(handleAuthResponse(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun refreshToken(): Flow<Result<ir.yar.login.domain.model.AuthResult>> = flow {
        emit(Result.Loading)
        try {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken == null) {
                emit(Result.Error("No refresh token available"))
                return@flow
            }

            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            emit(handleAuthResponse(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Token refresh failed"))
        }
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenManager.saveTokens(accessToken, refreshToken)
    }

    override suspend fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }

    override suspend fun getRefreshToken(): String? {
        return tokenManager.getRefreshToken()
    }

    override suspend fun clearTokens() {
        tokenManager.clearTokens()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }

    private suspend fun handleAuthResponse(response: Response<AuthResponseDTO>): Result<ir.yar.login.domain.model.AuthResult> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                saveTokens(body.token, body.refreshToken)

                Result.Success(
                    ir.yar.login.domain.model.AuthResult(
                        token = body.token,
                        user = User(
                            id = body.user.id,
                            phoneNumber = body.user.phoneNumber,
                            fullName = body.user.fullName,
                            profileImageUrl = body.user.profileImageUrl,
                            role = body.user.role,
                            enabled = body.user.enabled
                        )
                    )
                )
            } else {
                Result.Error("Empty response body")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val serverMessage = try {
                val json = org.json.JSONObject(errorBody ?: "")
                json.optString("message", "")
            } catch (e: Exception) {
                ""
            }

            Result.Error(
                message = serverMessage.ifEmpty { "Unknown error" },
                code = response.code()
            )
        }
    }
}