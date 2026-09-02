package ir.yar.anbar.data.mapper

import ir.yar.anbar.data.local.entity.InvoiceProductCrossRefEntity
import ir.yar.anbar.domain.model.InvoiceId
import ir.yar.anbar.domain.model.InvoiceProduct
import ir.yar.anbar.domain.model.ProductId
import ir.yar.anbar.domain.model.Quantity
import ir.yar.anbar.domain.model.type.Money

fun InvoiceProductCrossRefEntity.toDomain(): InvoiceProduct {
    return InvoiceProduct(
        invoiceId = InvoiceId(invoiceId),
        productId = ProductId(productId),
        quantity = Quantity(quantity),
        priceAtSale = Money(priceAtSale),
        costPriceAtTransaction = Money(costPriceAtTransaction),
        discount = Money(discount),
        total = Money(total)
    )
}

fun InvoiceProduct.toEntity(): InvoiceProductCrossRefEntity {
    return InvoiceProductCrossRefEntity(
        invoiceId = invoiceId.value,
        productId = productId.value,
        quantity = quantity.value,
        priceAtSale = priceAtSale.amount,
        costPriceAtTransaction = costPriceAtTransaction.amount,
        discount = discount.amount,
        total = total.amount
    )
}
