package com.example.login.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.remote.dto.request.LoginRequest
import com.example.login.data.remote.dto.request.RegisterRequest
import com.example.login.domain.model.AuthUiState
import com.example.login.domain.model.Result
import com.example.login.domain.repository.AuthRepository
import com.example.login.domain.usecase.LoginUseCase
import com.example.login.domain.usecase.LogoutUseCase
import com.example.login.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            loginUseCase(LoginRequest(phone, password)).collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = AuthUiState(isLoading = true)
                    }
                    is Result.Success -> {
                        _uiState.value = AuthUiState(data = result.data)
                    }
                    is Result.Error -> {
                        _uiState.value = AuthUiState(errorMessage = result.message)
                    }
                }
            }
        }
    }

    fun register(fullName: String, phone: String, password: String) {
        viewModelScope.launch {
            registerUseCase(
                RegisterRequest(
                    fullName = fullName,
                    phoneNumber = phone,
                    password = password
                )
            ).collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = AuthUiState(isLoading = true)
                    }
                    is Result.Success -> {
                        _uiState.value = AuthUiState(data = result.data)
                    }
                    is Result.Error -> {
                        _uiState.value = AuthUiState(errorMessage = result.message)
                    }
                }
            }
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }


    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            // Reset UI state after logout
            _uiState.value = AuthUiState()
        }
    }
}