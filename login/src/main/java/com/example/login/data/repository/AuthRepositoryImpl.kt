package com.example.login.data.repository

import com.example.login.domain.util.Resource
import com.example.login.data.remote.api.ApiAuthService
import com.example.login.data.remote.dto.request.LoginRequest
import com.example.login.data.remote.dto.request.RegisterRequest
import com.example.login.data.remote.dto.response.ErrorResponse
import com.example.login.data.remote.dto.response.LoginResponse
import com.example.login.data.remote.dto.response.RegisterResponse
import com.example.login.domain.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val authApiService: ApiAuthService
): AuthRepository {

    override fun register(
        phoneNumber: String,
        password: String,
        fullName: String
    ): Flow<Resource<RegisterResponse>> = flow {
        try {
            emit(Resource.Loading())

            val request = RegisterRequest(phoneNumber, password, fullName)
            val response = authApiService.register(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "Registration failed"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun login(phoneNumber: String, password: String): Flow<Resource<LoginResponse>> = flow {
        try {
            emit(Resource.Loading())

            val request = LoginRequest(phoneNumber, password)
            val response = authApiService.login(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "Login failed"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}