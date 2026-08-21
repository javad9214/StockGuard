package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.User
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(): Flow<Resource<User>>
}
