package com.example.composetrainer.data.mapper

import com.example.composetrainer.data.local.entity.ProductEntity
import com.example.composetrainer.domain.model.Product

object ProductMapper {
    fun toDomain(entity: ProductEntity): Product {
        return Product(
            id = entity.id,
            name = entity.name,
            barcode = entity.barcode,
            price = entity.price,
            costPrice = entity.costPrice,
            description = entity.description,
            image = entity.image,
            subCategoryId = entity.subcategoryId,
            supplierId = entity.supplierId,
            unit = entity.unit,
            date = entity.date,
            stock = entity.stock,
            minStockLevel = entity.minStockLevel,
            maxStockLevel = entity.maxStockLevel,
            isActive = entity.isActive,
            tags = entity.tags,
            lastSoldDate = entity.lastSoldDate
        )
    }

    fun toEntity(domain: Product): ProductEntity {
        return ProductEntity(
            id = domain.id,
            name = domain.name,
            barcode = domain.barcode,
            price = domain.price,
            costPrice = domain.costPrice,
            description = domain.description,
            image = domain.image,
            subcategoryId = domain.subCategoryId,
            supplierId = domain.supplierId,
            unit = domain.unit,
            stock = domain.stock,
            minStockLevel = domain.minStockLevel,
            maxStockLevel = domain.maxStockLevel,
            isActive = domain.isActive,
            tags = domain.tags,
            lastSoldDate = domain.lastSoldDate,
            date = domain.date,
            synced = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isDeleted = false
        )
    }
}