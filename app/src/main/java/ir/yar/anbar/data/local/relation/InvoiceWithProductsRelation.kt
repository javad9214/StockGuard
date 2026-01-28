package ir.yar.anbar.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import ir.yar.anbar.data.local.entity.InvoiceEntity
import ir.yar.anbar.data.local.entity.InvoiceProductCrossRefEntity
import ir.yar.anbar.data.local.entity.UserProductEntity

data class InvoiceWithProductsRelation (
    @Embedded val invoice: InvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId",
        entity = InvoiceProductCrossRefEntity::class
    )
    val invoiceProducts: List<ProductsRelation>,

    )

data class ProductsRelation(
    @Embedded val invoiceProductsCrossRef: InvoiceProductCrossRefEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: UserProductEntity
)