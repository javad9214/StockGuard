package com.example.login.domain.repository

import com.example.login.data.remote.dto.request.LoginRequest
import com.example.login.data.remote.dto.request.RegisterRequest
import com.example.login.domain.model.AuthResult
import com.example.login.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(request: RegisterRequest): Flow<Result<AuthResult>>
    suspend fun login(request: LoginRequest): Flow<Result<AuthResult>>
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun isLoggedIn(): Boolean
}