package com.example.login.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.domain.util.Resource
import com.example.login.data.remote.dto.response.LoginResponse
import com.example.login.data.remote.dto.response.RegisterResponse
import com.example.login.domain.usecase.LoginUseCase
import com.example.login.domain.usecase.RegisterUseCase
import com.example.login.data.local.CredentialsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val credentialsManager: CredentialsManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<LoginResponse>?>(null)
    val loginState: StateFlow<Resource<LoginResponse>?> = _loginState

    private val _registerState = MutableStateFlow<Resource<RegisterResponse>?>(null)
    val registerState: StateFlow<Resource<RegisterResponse>?> = _registerState

    private val _autoLoginAvailable = MutableStateFlow<Boolean?>(null)
    val autoLoginAvailable: StateFlow<Boolean?> = _autoLoginAvailable

    private val _skipLogin = MutableStateFlow<Boolean>(true)
    val skipLogin : StateFlow<Boolean> = _skipLogin

    init {
        _skipLogin.value = true
        checkStoredCredentials()
    }

    private fun checkStoredCredentials() {
        _autoLoginAvailable.value = credentialsManager.getCredentials() != null
    }

    fun login(phoneNumber: String, password: String, remember: Boolean = false) {
        loginUseCase(phoneNumber, password).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (remember) {
                        credentialsManager.saveCredentials(phoneNumber, password)
                    }
                }
                is Resource.Error -> {
                    credentialsManager.saveCredentials(phoneNumber, password) //skip fr now
                   // credentialsManager.clearCredentials()
                }
                else -> {}
            }
            _loginState.value = result
        }.launchIn(viewModelScope)
    }

    fun register(phoneNumber: String, password: String, fullName: String) {
        registerUseCase(phoneNumber, password, fullName).onEach { result ->
            _registerState.value = result
        }.launchIn(viewModelScope)
    }

    fun attemptAutoLogin() {
        credentialsManager.getCredentials()?.let { (phone, password) ->
            login(phone, password, true)
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }

    fun clearRegisterState() {
        _registerState.value = null
    }

    fun logout() {
        credentialsManager.clearCredentials()
        _autoLoginAvailable.value = false
    }
}