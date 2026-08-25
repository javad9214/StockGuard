package ir.yar.anbar.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import ir.yar.anbar.domain.model.AppVersionInfo

/**
 * Response DTO for app version information
 * Matches the backend AppVersionResponseDTO
 */
@Keep
data class AppVersionResponseDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("platform")
    val platform: String, // "ANDROID" or "IOS"

    @SerializedName("minVersionCode")
    val minVersionCode: Int,

    @SerializedName("lastVersionCode")
    val lastVersionCode: Int,

    @SerializedName("minVersionName")
    val minVersionName: String,

    @SerializedName("lastVersionName")
    val lastVersionName: String,

    @SerializedName("updateUrl")
    val updateUrl: String?,

    @SerializedName("releaseNotes")
    val releaseNotes: String?,

    @SerializedName("enabled")
    val enabled: Boolean,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?
)

fun AppVersionResponseDto.toDomain(): AppVersionInfo {
    return AppVersionInfo(
        platform = platform,
        minVersionCode = minVersionCode,
        lastVersionCode = lastVersionCode,
        minVersionName = minVersionName,
        lastVersionName = lastVersionName,
        updateUrl = updateUrl,
        releaseNotes = releaseNotes,
        enabled = enabled
    )
}
