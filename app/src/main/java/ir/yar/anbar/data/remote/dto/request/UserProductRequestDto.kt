package ir.yar.anbar.data.remote.dto.request

data class UserProductRequestDto(
    val catalogProductId: Long? = null,
    val barcode: String? = null,
    val customName: String? = null,
    val price: Long,
    val costPrice: Long,
    val description: String? = null,
    val subcategoryId: Int? = null,
    val supplierId: Int? = null,
    val unit: String? = null,
    val stock: Int? = 0,
    val minStockLevel: Int? = null,
    val maxStockLevel: Int? = null,
    val isActive: Boolean? = true,
    val tags: String? = null
)

