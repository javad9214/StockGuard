package ir.yar.login.domain.usecase

import ir.yar.login.data.remote.dto.request.RegisterRequest
import ir.yar.login.domain.model.AuthResult
import ir.yar.login.domain.model.Result
import ir.yar.login.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Flow<Result<ir.yar.login.domain.model.AuthResult>> {
        return repository.register(request)
    }
}
