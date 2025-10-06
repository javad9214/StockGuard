package com.example.login.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.domain.util.Resource
import com.example.login.data.remote.dto.response.LoginResponse
import com.example.login.data.remote.dto.response.RegisterResponse
import com.example.login.domain.usecase.LoginUseCase
import com.example.login.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<LoginResponse>?>(null)
    val loginState: StateFlow<Resource<LoginResponse>?> = _loginState

    private val _registerState = MutableStateFlow<Resource<RegisterResponse>?>(null)
    val registerState: StateFlow<Resource<RegisterResponse>?> = _registerState

    fun login(phoneNumber: String, password: String) {
        loginUseCase(phoneNumber, password).onEach { result ->
            _loginState.value = result
        }.launchIn(viewModelScope)
    }

    fun register(phoneNumber: String, password: String, fullName: String) {
        registerUseCase(phoneNumber, password, fullName).onEach { result ->
            _registerState.value = result
        }.launchIn(viewModelScope)
    }

    fun clearLoginState() {
        _loginState.value = null
    }

    fun clearRegisterState() {
        _registerState.value = null
    }


}