package ir.yar.anbar.data.local.entity

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
            entity = UserProductEntity::class,
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
data class InvoiceProductCrossRefEntity(
    val invoiceId: Long,
    val productId: Long,
    val quantity: Int,
    val priceAtSale: Long, // Selling price of the product
    val costPriceAtTransaction: Long, // // Cost price for Buying of the product
    val discount: Long = 0,
    val total: Long = (priceAtSale - discount) * quantity
)