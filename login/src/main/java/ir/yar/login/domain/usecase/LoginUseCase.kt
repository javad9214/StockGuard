package ir.yar.login.domain.usecase

import ir.yar.login.data.remote.dto.request.LoginRequest
import ir.yar.login.domain.model.AuthResult
import ir.yar.login.domain.model.Result
import ir.yar.login.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: LoginRequest): Flow<Result<ir.yar.login.domain.model.AuthResult>> {
        return repository.login(request)
    }
}
