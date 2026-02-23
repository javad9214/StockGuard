package ir.yar.anbar.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import ir.yar.anbar.data.local.entity.CategoryEntity
import ir.yar.anbar.data.local.entity.SubcategoryEntity

data class CategoryWithSubcategories(
    @Embedded val category: CategoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val subcategories: List<SubcategoryEntity>
)
