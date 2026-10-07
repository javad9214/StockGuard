package ir.yar.anbar.data.remote.dto.response

data class UserProductResponseDto(
    val id: Long,
    val userId: Long,
    val catalogProductId: Long? = null,
    val barcode: String? = null,
    val customName: String? = null,
    val price: Long,
    val costPrice: Long,
    val description: String? = null,
    val imageType: String? = null,
    val image: String? = null, // Base64-encoded image bytes sent back by the server
    // Catalog image URL for adopted rows without their own image; null when
    // the image travels inline above or the row has neither
    val imageUrl: String? = null,
    val subcategoryId: Int? = null,
    val categoryId: Int? = null,
    val subcategoryName: String? = null,
    val categoryName: String? = null,
    val supplierId: Int? = null,
    val unit: String? = null,
    val stock: Int,
    val minStockLevel: Int? = null,
    val maxStockLevel: Int? = null,
    val isActive: Boolean,
    val tags: String? = null,
    val synced: Boolean,
    val createdAt: String,
    val updatedAt: String
)