package com.example.composetrainer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val numberId: Long, // Starts from 10000
    val dateTime: Long // System.currentTimeMillis()
)
