package ir.yar.anbar.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import ir.yar.anbar.data.local.entity.UserProductEntity
import ir.yar.anbar.data.local.entity.SubcategoryEntity

data class SubcategoryWithProducts(
    @Embedded val subcategory: SubcategoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "subcategoryId"
    )
    val products: List<UserProductEntity>
)
