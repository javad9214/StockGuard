package com.example.login.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("id")
    val id: Long,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,

    @SerializedName("role")
    val role: String,

    @SerializedName("enabled")
    val enabled: Boolean,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("lastLogin")
    val lastLogin: String? = null
)