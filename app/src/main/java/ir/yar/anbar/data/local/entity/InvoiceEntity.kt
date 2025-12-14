package ir.yar.anbar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val prefix: String = "INV",         // Fixed prefix, e.g., "INV"
    val invoiceNumber: Long,             // e.g., 12
    val invoiceDate: Long = System.currentTimeMillis(),  //	Let user select invoice date (may differ from createdAt)
    val invoiceType: String? = null,    // e.g., "S", "B" — optional for future
    val customerId: Long? = null,   // e.g., "C005" — optional for future

    val totalAmount: Long? = null,
    val totalProfit: Long? = null,
    val totalDiscount: Long = 0,

    val status: String? = null,
    val paymentMethod: String? = null,
    val notes: String? = null,

    val synced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)