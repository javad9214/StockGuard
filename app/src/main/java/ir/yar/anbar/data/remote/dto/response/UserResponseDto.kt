package ir.yar.anbar.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import ir.yar.anbar.data.remote.api.ApiConstants
import ir.yar.anbar.domain.model.User
import java.time.LocalDateTime

@Keep
data class UserResponseDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("enabled")
    val enabled: Boolean? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("lastLogin")
    val lastLogin: String? = null
)

fun UserResponseDto.toDomain(): User = User(
    id = id,
    phoneNumber = phoneNumber,
    fullName = fullName,
    profileImageUrl = profileImageUrl.toAbsoluteUrl(),
    role = role.orEmpty(),
    enabled = enabled ?: true,
    createdAt = createdAt.toLocalDateTimeOrNull(),
    lastLogin = lastLogin.toLocalDateTimeOrNull()
)

// The server may expose profile images as absolute or server-relative paths
private fun String?.toAbsoluteUrl(): String? {
    if (isNullOrBlank()) return null
    return if (startsWith("http")) this else ApiConstants.BASE_URL + trimStart('/')
}

private fun String?.toLocalDateTimeOrNull(): LocalDateTime? = runCatching {
    LocalDateTime.parse(orEmpty().trimEnd('Z').replace(' ', 'T'))
}.getOrNull()
