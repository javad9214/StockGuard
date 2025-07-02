package com.example.composetrainer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val barcode: String?,
    val price: Long?,
    val costPrice: Long?,
    val description: String?,
    val image: String?,
    val subcategoryId: Int?,
    val supplierId: Int?,
    val unit: String?,
    val stock: Int,
    val minStockLevel: Int?,
    val maxStockLevel: Int?,
    val isActive: Boolean = true,
    val tags: String?,
    val lastSoldDate: Long?,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)


