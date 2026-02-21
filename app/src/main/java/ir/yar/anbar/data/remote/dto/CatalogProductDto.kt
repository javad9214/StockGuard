package ir.yar.anbar.data.remote.dto


data class CatalogProductDto(
    val id: Long,
    val name: String,
    val barcode: String?,
    val description: String?,
    val brand: String?,
    val manufacturer: String?,

    // Category
    val subcategoryId: Int?,
    val subcategoryName: String?,
    val categoryName: String?,

    // Image
    val imageUrl: String?,
    val imageSource: String?,

    // Price & Unit
    val suggestedPrice: Long?,
    val unit: String?,

    // Meta
    val tags: String?,
    val status: CatalogStatus,

    // Stats
    val qualityScore: Int,
    val adoptionCount: Int,

    // External
    val externalSource: String,
    val externalSourceId: Long,

    // Lifecycle
    val createdAt: String,  // ISO-8601 LocalDateTime as String
    val updatedAt: String,
)

enum class CatalogStatus {
    VERIFIED,
    PENDING_REVIEW,
    REJECTED,
    DRAFT
}
