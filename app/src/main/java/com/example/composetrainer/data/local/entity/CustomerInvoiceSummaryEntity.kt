package com.example.composetrainer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_invoice_summary")
data class CustomerInvoiceSummaryEntity(
    @PrimaryKey val customerId: Long,
    val totalInvoices: Int = 0,
    val totalAmount: Long = 0L,
    val totalPaid: Long = 0L,
    val totalDebt: Long = totalAmount - totalPaid,
    val lastInvoiceDate: Long? = null,

    val updatedAt: Long = System.currentTimeMillis()
)
