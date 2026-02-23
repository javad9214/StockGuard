package ir.yar.login.domain.usecase


import ir.yar.login.domain.repository.AuthRepository
import javax.inject.Inject


class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.clearTokens()
    }
}