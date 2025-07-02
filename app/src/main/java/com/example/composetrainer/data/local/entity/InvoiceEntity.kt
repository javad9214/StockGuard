package com.example.composetrainer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val prefix: String = "INV",         // Fixed prefix, e.g., "INV"
    val invoiceNumber: Long,             // e.g., 12

    val invoiceType: String? = null,    // e.g., "S", "B" — optional for future
    val customerId: Long? = null,   // e.g., "C005" — optional for future

    val totalAmount: Long? = null,
    val status: String? = null,
    val paymentMethod: String? = null,
    val notes: String? = null,


    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)