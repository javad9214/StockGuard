package com.example.login.domain.repository

import com.example.login.domain.util.Resource
import com.example.login.data.remote.dto.response.LoginResponse
import com.example.login.data.remote.dto.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun register(
        phoneNumber: String,
        password: String,
        fullName: String
    ): Flow<Resource<RegisterResponse>>

    fun login(
        phoneNumber: String,
        password: String
    ): Flow<Resource<LoginResponse>>
}