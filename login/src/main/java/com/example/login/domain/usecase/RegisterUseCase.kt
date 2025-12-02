package com.example.login.domain.usecase

import com.example.login.data.remote.dto.request.RegisterRequest
import com.example.login.domain.model.AuthResult
import com.example.login.domain.model.Result
import com.example.login.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Flow<Result<AuthResult>> {
        return repository.register(request)
    }
}
