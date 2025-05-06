package com.example.composetrainer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val prefix: String = "INV",         // Fixed prefix, e.g., "INV"
    val invoiceDate: String,            // In format like "1403-02-16" (Jalali)
    val invoiceNumber: Int,             // e.g., 12

    val invoiceType: String? = null,    // e.g., "S", "B" — optional for future
    val customerCode: String? = null,   // e.g., "C005" — optional for future

    val createdAt: Long = System.currentTimeMillis()
)