package ir.yar.anbar.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CatalogProductDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("barcode")
    val barcode: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("brand")
    val brand: String?,

    @SerializedName("manufacturer")
    val manufacturer: String?,

    // Category
    @SerializedName("subcategoryId")
    val subcategoryId: Int?,

    @SerializedName("subcategoryName")
    val subcategoryName: String?,

    @SerializedName("categoryName")
    val categoryName: String?,

    // Image
    @SerializedName("imageUrl")
    val imageUrl: String?,

    @SerializedName("imageSource")
    val imageSource: String?,

    // Price & Unit
    @SerializedName("suggestedPrice")
    val suggestedPrice: Long?,

    @SerializedName("unit")
    val unit: String?,

    // Meta
    @SerializedName("tags")
    val tags: String?,

    @SerializedName("status")
    val status: CatalogStatus,

    // Stats
    @SerializedName("qualityScore")
    val qualityScore: Int,

    @SerializedName("adoptionCount")
    val adoptionCount: Int,

    // External
    @SerializedName("externalSource")
    val externalSource: String,

    @SerializedName("externalSourceId")
    val externalSourceId: Long,

    // Lifecycle
    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,
)

@Keep
enum class CatalogStatus {
    @SerializedName("VERIFIED")
    VERIFIED,

    @SerializedName("PENDING_REVIEW")
    PENDING_REVIEW,

    @SerializedName("REJECTED")
    REJECTED,

    @SerializedName("DRAFT")
    DRAFT
}
