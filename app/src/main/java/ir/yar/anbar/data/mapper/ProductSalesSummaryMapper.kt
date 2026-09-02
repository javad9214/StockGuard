package ir.yar.anbar.data.mapper

import ir.yar.anbar.data.local.entity.ProductSalesSummaryEntity
import ir.yar.anbar.domain.model.ProductId
import ir.yar.anbar.domain.model.ProductSalesSummary
import ir.yar.anbar.domain.model.ProductSalesSummaryId
import ir.yar.anbar.domain.model.SalesQuantity
import ir.yar.anbar.domain.model.type.Money
import java.time.Instant
import java.time.ZoneId

fun ProductSalesSummaryEntity.toDomain(): ProductSalesSummary {
    return ProductSalesSummary(
        id = ProductSalesSummaryId(id),
        productId = ProductId(productId),
        date = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate(),
        totalSold = SalesQuantity(totalSold),
        totalRevenue = Money(totalRevenue),
        totalCost = Money(totalCost),
        createdAt = Instant.ofEpochMilli(createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime(),
        updatedAt = Instant.ofEpochMilli(updatedAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime(),
        synced = synced
    )
}

fun ProductSalesSummary.toEntity(): ProductSalesSummaryEntity {
    return ProductSalesSummaryEntity(
        id = id.value,
        productId = productId.value,
        date = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        totalSold = totalSold.value,
        totalRevenue = totalRevenue.amount,
        totalCost = totalCost.amount,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        synced = synced,
        isDeleted = false
    )
}
