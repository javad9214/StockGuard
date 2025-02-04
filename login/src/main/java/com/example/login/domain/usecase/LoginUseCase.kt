package com.example.login.domain.usecase

import com.example.login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> =
        loginRepository.login(username, password)

}