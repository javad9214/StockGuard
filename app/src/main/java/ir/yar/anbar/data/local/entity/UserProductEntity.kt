package ir.yar.anbar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_products")
data class UserProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    val serverId: Long? = null, // Server ID after sync
    val catalogProductId: Long? = null, // NULL = custom product

    val name: String, // ADDED BACK - Always show product name
    val barcode: String?, // ADDED BACK - Essential for scanning
    val customName: String?, // Optional override for catalog products

    val price: Long, // User's selling price
    val costPrice: Long, // User's cost price

    val description: String?,
    val image: String?,

    val subcategoryId: Int?,
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
)