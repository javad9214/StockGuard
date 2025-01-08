package com.example.login.domain.repository

interface LoginRepository {
    suspend fun login(username: String, password: String): Result<Unit>
}