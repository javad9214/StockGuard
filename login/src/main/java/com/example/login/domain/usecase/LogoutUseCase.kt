package com.example.login.domain.usecase


import com.example.login.domain.repository.AuthRepository
import javax.inject.Inject


class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.clearToken()
    }
}