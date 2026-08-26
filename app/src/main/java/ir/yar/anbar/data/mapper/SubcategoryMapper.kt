package ir.yar.anbar.data.mapper

import ir.yar.anbar.data.local.entity.SubcategoryEntity
import ir.yar.anbar.domain.model.Subcategory

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

fun List<SubcategoryEntity>.toDomain(): List<Subcategory> {
    return this.map { it.toDomain() }
}

fun List<Subcategory>.toEntity(): List<SubcategoryEntity> {
    return this.map { it.toEntity() }
}
