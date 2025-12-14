package ir.yar.login.data.remote.interceptor

import ir.yar.login.data.remote.api.ApiAuthService
import ir.yar.login.data.remote.dto.request.RefreshTokenRequest
import ir.yar.login.data.repository.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiAuthService: ApiAuthService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // If response is 401, try to refresh token
        if (response.code == 401) {
            synchronized(this) {
                val refreshToken = runBlocking { tokenManager.getRefreshToken() }

                if (refreshToken == null) {
                    // No refresh token, logout
                    runBlocking { tokenManager.clearTokens() }
                    return null
                }

                return try {
                    val refreshResponse = runBlocking {
                        apiAuthService.refreshToken(RefreshTokenRequest(refreshToken))
                    }

                    if (refreshResponse.isSuccessful) {
                        val newTokens = refreshResponse.body()
                        if (newTokens != null) {
                            runBlocking {
                                tokenManager.saveTokens(newTokens.token, newTokens.refreshToken)
                            }

                            // Retry the request with new token
                            response.request.newBuilder()
                                .header("Authorization", "Bearer ${newTokens.token}")
                                .build()
                        } else {
                            runBlocking { tokenManager.clearTokens() }
                            null
                        }
                    } else {
                        // Refresh failed, logout
                        runBlocking { tokenManager.clearTokens() }
                        null
                    }
                } catch (e: Exception) {
                    runBlocking { tokenManager.clearTokens() }
                    null
                }
            }
        }

        return null
    }
}