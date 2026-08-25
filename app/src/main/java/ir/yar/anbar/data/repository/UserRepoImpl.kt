package ir.yar.anbar.data.repository

import ir.yar.anbar.data.remote.api.ApiServiceUser
import ir.yar.anbar.data.remote.dto.response.toDomain
import ir.yar.anbar.data.remote.util.ApiResponseHandler
import ir.yar.anbar.domain.model.User
import ir.yar.anbar.domain.repository.UserRepository
import ir.yar.anbar.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val apiServiceUser: ApiServiceUser
) : UserRepository {

    override fun getUserProfile(): Flow<Resource<User>> =
        ApiResponseHandler.handleApiResponse(
            apiCall = { apiServiceUser.getUserProfile() },
            mapper = { it.toDomain() }
        )
}
