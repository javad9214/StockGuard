package com.example.composetrainer.data.local.relation

import androidx.room.Embedded
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.entity.InvoiceProductCrossRefEntity

data class InvoiceWithProductsRelation (
    val invoiceId: Long,
    @Embedded val invoice: InvoiceEntity,
    @Embedded val invoiceProducts: List<InvoiceProductCrossRefEntity>,

)