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
            image = entity.image,
            subCategoryId = entity.subcategoryId,
            date = entity.date,
            stock = entity.stock
        )
    }

    fun toEntity(domain: Product): ProductEntity {
        return ProductEntity(
            id = domain.id,
            name = domain.name,
            barcode = domain.barcode,
            price = domain.price,
            image = domain.image,
            subcategoryId = domain.subCategoryId,
            date = domain.date,
            stock = domain.stock
        )
    }
}