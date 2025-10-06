package com.example.login.domain.usecase

import com.example.login.domain.util.Resource
import com.example.login.data.remote.dto.response.RegisterResponse
import com.example.login.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(
        phoneNumber: String,
        password: String,
        fullName: String
    ): Flow<Resource<RegisterResponse>> {
        // Add validation here if needed
        if (phoneNumber.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Resource.Error("PhoneNumber cannot be empty"))
            }
        }

        if (password.length < 6) {
            return kotlinx.coroutines.flow.flow {
                emit(Resource.Error("Password must be at least 6 characters"))
            }
        }

        if (fullName.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Resource.Error("Full name cannot be empty"))
            }
        }

        return repository.register(phoneNumber, password, fullName)
    }
}