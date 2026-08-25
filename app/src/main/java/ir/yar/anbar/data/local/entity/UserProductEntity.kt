package ir.yar.anbar.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_products",
    indices = [
        // Server-sync merges look rows up by serverId; search scans by barcode
        Index(value = ["serverId"]),
        Index(value = ["barcode"])
    ]
)
data class UserProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    val serverId: Long? = null,
    val catalogProductId: Long? = null,

    val name: String,
    val barcode: String?,
    val customName: String?, // Optional override for catalog products

    val price: Long, // User's selling price
    val costPrice: Long, // User's cost price

    val description: String?,

    val imageLocalPath: String?,
    val imageUrl: String?,

    val subcategoryId: Int?,
    // Display name cached from the server response — the local subcategories
    // table is never synced, so a join can't resolve it.
    val subcategoryName: String?,
    val supplierId: Int?,

    val unit: String?,
    val stock: Int,
    val minStockLevel: Int?,
    val maxStockLevel: Int?,

    val isActive: Boolean = true,
    val tags: String?,
    val lastSoldDate: Long?,
    val date: Long,

    val syncStatus: String = "SYNCED", // SYNCED, PENDING_CREATE, PENDING_UPDATE, PENDING_DELETE
    val synced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    companion object {
        const val SYNC_STATUS_SYNCED = "SYNCED"
        const val SYNC_STATUS_PENDING_CREATE = "PENDING_CREATE"
        const val SYNC_STATUS_PENDING_UPDATE = "PENDING_UPDATE"
        const val SYNC_STATUS_PENDING_DELETE = "PENDING_DELETE"
    }
}