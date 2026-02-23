package ir.yar.anbar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_products")
data class CatalogProductEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val barcode: String?,
    val description: String?,
    val brand: String?,
    val manufacturer: String?,
    val category: String?,
    val subcategory: String?,
    val imageUrl: String?,
    val suggestedPrice: Long?,
    val unit: String?,
    val tags: String?,
    val status: String, // VERIFIED, PENDING_REVIEW, REJECTED
    val qualityScore: Int = 0,
    val adoptionCount: Int = 0,
    val isActive: Boolean = true,
    val cachedAt: Long = System.currentTimeMillis()
)