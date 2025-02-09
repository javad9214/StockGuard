package com.example.composetrainer.data.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.entity.InvoiceProductCrossRef
import com.example.composetrainer.data.local.entity.ProductEntity

data class InvoiceWithProducts (
    @Embedded val invoice: InvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = InvoiceProductCrossRef::class,
            parentColumn = "invoiceId",
            entityColumn = "productId"
        )
    )
    val products: List<ProductWithQuantity>
)

data class ProductWithQuantity(
    @Embedded val product: ProductEntity,
    val quantity: Int
)