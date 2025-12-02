package com.example.login.data.repository

import com.example.login.data.remote.api.ApiAuthService
import com.example.login.data.remote.dto.request.LoginRequest
import com.example.login.data.remote.dto.request.RegisterRequest
import com.example.login.data.remote.dto.response.AuthResponseDTO
import com.example.login.domain.model.AuthResult
import com.example.login.domain.model.Result
import com.example.login.domain.model.User
import com.example.login.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiAuthService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun register(request: RegisterRequest): Flow<Result<AuthResult>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.register(request)
            emit(handleAuthResponse(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun login(request: LoginRequest): Flow<Result<AuthResult>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.login(request)
            emit(handleAuthResponse(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun saveToken(token: String) {
        tokenManager.saveToken(token)
    }

    override suspend fun getToken(): String? {
        return tokenManager.getToken()
    }

    override suspend fun clearToken() {
        tokenManager.clearToken()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }

    private suspend fun handleAuthResponse(response: Response<AuthResponseDTO>): Result<AuthResult> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {

                // Save token automatically
                saveToken(body.token)

                Result.Success(
                    AuthResult(
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
            // Extract server error message from response.errorBody()
            val errorBody = response.errorBody()?.string()

            val serverMessage =
                try {
                    val json = org.json.JSONObject(errorBody ?: "")
                    json.optString("message", "")
                } catch (e: Exception) {
                    ""
                }


            Result.Error(
                message = serverMessage ?: "Unknown error",
                code = response.code()
            )
        }
    }

}