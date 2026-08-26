package ir.yar.anbar.data.mapper

import ir.yar.anbar.data.local.entity.StockMovementEntity
import ir.yar.anbar.domain.model.InvoiceId
import ir.yar.anbar.domain.model.MovementNote
import ir.yar.anbar.domain.model.MovementReason
import ir.yar.anbar.domain.model.ProductId
import ir.yar.anbar.domain.model.QuantityChange
import ir.yar.anbar.domain.model.StockMovement
import ir.yar.anbar.domain.model.StockMovementId
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun StockMovementEntity.toDomain(): StockMovement {
    return StockMovement(
        id = StockMovementId(id),
        productId = ProductId(productId),
        quantityChange = QuantityChange(quantityChange),
        reason = MovementReason.fromCode(reason) ?: MovementReason.MANUAL_ADJUST,
        sourceInvoiceId = sourceInvoiceId?.let { InvoiceId(it) },
        note = note?.let { MovementNote(it) },
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneId.systemDefault()
        ),
        synced = synced
    )
}

fun StockMovement.toEntity(): StockMovementEntity {
    return StockMovementEntity(
        id = id.value,
        productId = productId.value,
        quantityChange = quantityChange.value,
        reason = reason.code,
        sourceInvoiceId = sourceInvoiceId?.value,
        note = note?.value,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        synced = synced
    )
}
