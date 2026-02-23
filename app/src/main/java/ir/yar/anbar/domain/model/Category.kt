package ir.yar.anbar.domain.model

import ir.yar.anbar.data.local.entity.CategoryEntity
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

// Domain Model
data class Category(
    val id: CategoryId,
    val name: CategoryName,
    val icon: CategoryIcon?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    // Business logic methods
    fun isRecentlyCreated(): Boolean {
        return createdAt.isAfter(LocalDateTime.now().minusDays(7))
    }

    fun isRecentlyUpdated(): Boolean {
        return updatedAt.isAfter(createdAt.plusMinutes(1))
    }

    fun hasIcon(): Boolean = icon != null

    // Helper method to get timestamp for database storage
    fun getCreatedAtTimestamp(): Long {
        return createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    // Helper method to get timestamp for database storage
    fun getUpdatedAtTimestamp(): Long {
        return updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}

// Value Objects for type safety and validation
@JvmInline
value class CategoryId(val value: Int) {
    init {
        require(value > 0) { "Category ID must be positive" }
    }
}

@JvmInline
value class CategoryName(val value: String) {
    init {
        require(value.isNotBlank()) { "Category name cannot be blank" }
        require(value.length <= 100) { "Category name cannot exceed 100 characters" }
    }
}

@JvmInline
value class CategoryIcon(val value: String) {
    init {
        require(value.isNotBlank()) { "Category icon cannot be blank" }
    }
}

// Mapping Extension Functions
fun CategoryEntity.toDomain(): Category {
    return Category(
        id = CategoryId(id),
        name = CategoryName(name),
        icon = icon?.let { CategoryIcon(it) },
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneId.systemDefault()
        ),
        updatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(updatedAt),
            ZoneId.systemDefault()
        )
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id.value,
        name = name.value,
        icon = icon?.value,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isDeleted = false // Domain doesn't handle soft deletion
    )
}

// Helper function for creating new categories
object CategoryFactory {
    fun create(
        name: String,
        icon: String? = null
    ): Category {
        val now = LocalDateTime.now()
        return Category(
            id = CategoryId(0), // Will be set by database
            name = CategoryName(name),
            icon = icon?.let { CategoryIcon(it) },
            createdAt = now,
            updatedAt = now
        )
    }
}

// Extension for updating category
fun Category.updateName(newName: String): Category {
    return copy(
        name = CategoryName(newName),
        updatedAt = LocalDateTime.now()
    )
}

fun Category.updateIcon(newIcon: String?): Category {
    return copy(
        icon = newIcon?.let { CategoryIcon(it) },
        updatedAt = LocalDateTime.now()
    )
}