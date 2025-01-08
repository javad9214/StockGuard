package com.example.login.data.repository

import com.example.login.domain.repository.LoginRepository
import kotlinx.coroutines.delay

class LoginRepositoryImpl: LoginRepository {

    override suspend fun login(username: String, password: String): Result<Unit> {
        // Simulate a network delay
        delay(2000)
        return if (username == "admin" && password == "password") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Invalid username or password"))
        }
    }
}