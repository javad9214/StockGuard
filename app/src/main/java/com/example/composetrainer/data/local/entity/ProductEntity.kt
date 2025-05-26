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
    val image: String?,
    val subcategoryId: Int?,
    val date: Long,
    val stock: Int
)


