package com.example.composetrainer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val productId: Long,
    val quantityChange: Int, // + for add, - for remove
    val reason: String, // e.g., "SALE", "PURCHASE", "MANUAL_ADJUST", etc.
    val sourceInvoiceId: Long? = null, // link to Invoice if applicable
    val note: String? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)