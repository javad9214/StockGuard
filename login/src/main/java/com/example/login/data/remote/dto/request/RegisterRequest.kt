package com.example.login.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("deviceToken")
    val deviceToken: String? = null,

    @SerializedName("deviceId")
    val deviceId: String? = null
)