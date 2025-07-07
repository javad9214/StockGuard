package com.example.composetrainer.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "invoice_products",
    primaryKeys = ["invoiceId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["invoiceId"]),
        Index(value = ["productId"])
    ]
)
data class InvoiceProductCrossRef(
    val invoiceId: Long,
    val productId: Long,
    val quantity: Int,
    val priceAtSale: Long, // Snapshot of product price at time of sale
    val discount: Long = 0,
    val total: Long = (priceAtSale - discount) * quantity
)