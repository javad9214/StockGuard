package com.example.login.domain.model

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: AuthResult? = null
)
