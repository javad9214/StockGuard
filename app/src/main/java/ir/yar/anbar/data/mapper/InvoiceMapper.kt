package ir.yar.anbar.data.mapper

import ir.yar.anbar.data.local.entity.InvoiceEntity
import ir.yar.anbar.domain.model.CustomerId
import ir.yar.anbar.domain.model.Invoice
import ir.yar.anbar.domain.model.InvoiceId
import ir.yar.anbar.domain.model.InvoiceNumber
import ir.yar.anbar.domain.model.InvoicePrefix
import ir.yar.anbar.domain.model.InvoiceStatus
import ir.yar.anbar.domain.model.InvoiceType
import ir.yar.anbar.domain.model.Note
import ir.yar.anbar.domain.model.PaymentMethod
import ir.yar.anbar.domain.model.type.Money
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun InvoiceEntity.toDomain(): Invoice {
    return Invoice(
        id = InvoiceId(id),
        prefix = InvoicePrefix(prefix),
        invoiceNumber = InvoiceNumber(invoiceNumber),
        invoiceDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(invoiceDate),
            ZoneId.systemDefault()
        ),
        invoiceType = InvoiceType.fromCode(invoiceType),
        customerId = customerId?.let { CustomerId(it) },
        totalAmount = totalAmount?.let { Money(it) },
        totalProfit = totalProfit?.let { Money(it) },
        totalDiscount = Money(totalDiscount),
        status = InvoiceStatus.fromString(status),
        paymentMethod = PaymentMethod.fromString(paymentMethod),
        notes = notes?.let { Note(it) },
        synced = synced,
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneId.systemDefault()
        ),
        updatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(updatedAt),
            ZoneId.systemDefault()
        )
    )
}

fun Invoice.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = id.value,
        prefix = prefix.value,
        invoiceNumber = invoiceNumber.value,
        invoiceDate = invoiceDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        invoiceType = invoiceType?.code,
        customerId = customerId?.value,
        totalAmount = totalAmount?.amount,
        totalProfit = totalProfit?.amount,
        totalDiscount = totalDiscount.amount,
        status = status?.name,
        paymentMethod = paymentMethod?.name,
        notes = notes?.value,
        synced = synced,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isDeleted = false
    )
}
