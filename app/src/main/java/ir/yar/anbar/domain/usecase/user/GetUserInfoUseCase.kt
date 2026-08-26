package ir.yar.anbar.domain.usecase.user

import ir.yar.anbar.domain.model.User
import ir.yar.anbar.domain.repository.UserRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<User>> {
        return userRepository.getUserProfile()
    }
}
