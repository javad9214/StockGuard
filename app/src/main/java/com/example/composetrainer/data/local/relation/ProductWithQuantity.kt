package com.example.composetrainer.data.local.relation

import androidx.room.Embedded
import com.example.composetrainer.data.local.entity.ProductEntity
import com.example.composetrainer.data.mapper.ProductMapper

data class ProductWithQuantity(
    @Embedded val product: ProductEntity,
    val quantity: Int
) {
    fun toDomain(): ProductWithQuantity {
        return ProductWithQuantity(
            product = ProductMapper.toDomain(product),
            quantity = quantity
        )
    }
}
