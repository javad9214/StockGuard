package com.example.composetrainer.domain.model

import com.example.composetrainer.data.local.entity.SubcategoryEntity

// Domain Model
data class Subcategory(
    val id: Int,
    val name: String,
    val categoryId: Int,
    val icon: String?,
    val description: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean
)

// Mapping Extension Functions
fun SubcategoryEntity.toDomain(): Subcategory {
    return Subcategory(
        id = this.id,
        name = this.name,
        categoryId = this.categoryId,
        icon = this.icon,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isDeleted = this.isDeleted
    )
}

fun Subcategory.toEntity(): SubcategoryEntity {
    return SubcategoryEntity(
        id = this.id,
        name = this.name,
        categoryId = this.categoryId,
        icon = this.icon,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isDeleted = this.isDeleted
    )
}

// List mapping extensions
fun List<SubcategoryEntity>.toDomain(): List<Subcategory> {
    return this.map { it.toDomain() }
}

fun List<Subcategory>.toEntity(): List<SubcategoryEntity> {
    return this.map { it.toEntity() }
}