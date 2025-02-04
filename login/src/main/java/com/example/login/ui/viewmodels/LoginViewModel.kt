package com.example.login.ui.viewmodels


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    val username: State<String> get() = _username
    val password: State<String> get() = _password

    private val _username = mutableStateOf("")
    private val _password = mutableStateOf("")

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (validateInputs()){
                val result = loginUseCase(username.value, password.value)
                if(result.isSuccess){
                    onSuccess()
                }else{
                    _errorMessage.value
                }
            }
        }
    }

    private fun validateInputs():Boolean{
        return when{
            username.value.isBlank() -> {
                _errorMessage.value = "Username cannot be empty"
                false
            }
            password.value.isBlank() -> {
                _errorMessage.value = "Password cannot be empty"
                false
            }
            else -> true
        }
    }
}