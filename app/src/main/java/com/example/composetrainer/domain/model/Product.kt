package com.example.composetrainer.domain.model

data class Product(
    val id: Long,
    val name: String,
    val barcode: String?,
    val price: Long?,
    val costPrice: Long?,
    val description: String?,
    val image: String?,
    val subCategoryId: Int?,
    val supplierId: Int?,
    val unit: String?,
    val date: Long,
    val stock: Int,
    val minStockLevel: Int?,
    val maxStockLevel: Int?,
    val isActive: Boolean = true,
    val tags: String?,
    val lastSoldDate: Long?
)