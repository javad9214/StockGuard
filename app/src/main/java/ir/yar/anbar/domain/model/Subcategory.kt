package ir.yar.anbar.domain.model


// Domain Model
data class Subcategory(
    val id: Int,
    val name: String,
    val categoryId: Int,
    val icon: String?,
    val description: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean
)