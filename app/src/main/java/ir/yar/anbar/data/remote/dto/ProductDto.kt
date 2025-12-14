package ir.yar.anbar.data.remote.dto

data class ProductDto(
    val id: Long,
    val name: String,
    val barcode: String?,

    val price: Long?, // Selling price of the product
    val costPrice: Long?, // Cost price for Buying of the product

    val description: String?,
    val image: String?,

    val subcategoryId: Int?,
    val supplierId: Int?,

    val unit: String?,
    val stock: Int,
    val minStockLevel: Int?,
    val maxStockLevel: Int?,

    val isActive: Boolean,
    val tags: String?,
    val lastSoldDate: Long?,
    val date: Long,

    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean
)