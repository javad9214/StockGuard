package com.example.login.domain.model

data class AuthResult(
    val token: String,
    val user: User
)