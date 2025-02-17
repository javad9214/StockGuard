package com.example.composetrainer.data.local.relation

import androidx.room.Embedded
import com.example.composetrainer.data.local.entity.ProductEntity
import com.example.composetrainer.data.mapper.ProductMapper
import com.example.composetrainer.domain.model.ProductWithQuantity

data class EntityProductWithQuantity(
    @Embedded val product: ProductEntity,
    val quantity: Int
) {
    fun toDomain(): ProductWithQuantity {
        return ProductWithQuantity(
            product = ProductMapper.toDomain(product), // Convert ProductEntity to Product
            quantity = quantity
        )
    }
}
