package ir.yar.anbar.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ApiResponseDto<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("error")
    val error: String? = null
)