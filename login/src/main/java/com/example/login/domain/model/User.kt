package com.example.login.domain.model

data class User(
    val id: Long,
    val phoneNumber: String,
    val fullName: String,
    val profileImageUrl: String?,
    val role: String,
    val enabled: Boolean
)