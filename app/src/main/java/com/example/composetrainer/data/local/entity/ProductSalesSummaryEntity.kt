package com.example.composetrainer.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_sales_summary",
    indices = [Index(value = ["productId", "date"], unique = true)]
)
data class ProductSalesSummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val productId: Long,
    val date: Long, // Save as System.currentTimeMillis()
    val totalSold: Int,
    val totalRevenue: Long = 0L, // Saves you from calculating totalSold * price each time
    val totalCost: Long = 0L, // For profit margin tracking
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false,
    val isDeleted: Boolean = false
)
