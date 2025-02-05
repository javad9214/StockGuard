package com.example.composetrainer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1001,
    val name: String,
    val barcode: String?,
    val price: Long?,
    val image: String?,
    val categoryID: Int?
)


