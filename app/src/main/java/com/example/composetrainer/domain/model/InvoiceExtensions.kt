package com.example.composetrainer.domain.model

import com.example.composetrainer.data.local.entity.InvoiceEntity


fun InvoiceEntity.buildInvoiceCode(): String {
    return listOfNotNull(
        prefix,
        invoiceType,
        invoiceDate,
        customerCode,
        invoiceNumber.toString().padStart(4, '0')
    ).joinToString("-")
}