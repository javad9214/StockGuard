package ir.yar.anbar.data.remote.dto.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

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

/**
 * Domain model for app version
 */
data class AppVersionInfo(
    val platform: String,
    val minVersionCode: Int,
    val lastVersionCode: Int,
    val minVersionName: String,
    val lastVersionName: String,
    val updateUrl: String?,
    val releaseNotes: String?,
    val enabled: Boolean
)

/**
 * Update status enum
 */
enum class UpdateStatus {
    UP_TO_DATE,           // Current version is the latest
    UPDATE_AVAILABLE,     // Update available but not required
    UPDATE_REQUIRED,      // Force update required
    UPDATE_DISABLED       // Version checking is disabled
}

/**
 * Extension function to convert DTO to domain model
 */
fun AppVersionResponseDto.toDomainModel(): AppVersionInfo {
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

/**
 * Check update status based on current version code
 */
fun AppVersionInfo.checkUpdateStatus(currentVersionCode: Int): UpdateStatus {
    return when {
        !enabled -> UpdateStatus.UPDATE_DISABLED
        currentVersionCode < minVersionCode -> UpdateStatus.UPDATE_REQUIRED
        currentVersionCode < lastVersionCode -> UpdateStatus.UPDATE_AVAILABLE
        else -> UpdateStatus.UP_TO_DATE
    }
}