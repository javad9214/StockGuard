package com.example.login.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AuthResponseDTO(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: UserDTO,

    @SerializedName("message")
    val message: String? = null
)