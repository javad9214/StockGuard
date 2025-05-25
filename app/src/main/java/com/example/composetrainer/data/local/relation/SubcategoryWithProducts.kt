package com.example.composetrainer.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.composetrainer.data.local.entity.ProductEntity
import com.example.composetrainer.data.local.entity.SubcategoryEntity

data class SubcategoryWithProducts(
    @Embedded val subcategory: SubcategoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "subcategoryId"
    )
    val products: List<ProductEntity>
)
