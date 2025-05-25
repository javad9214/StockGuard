package com.example.composetrainer.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.composetrainer.data.local.entity.CategoryEntity
import com.example.composetrainer.data.local.entity.SubcategoryEntity

data class CategoryWithSubcategories(
    @Embedded val category: CategoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val subcategories: List<SubcategoryEntity>
)
