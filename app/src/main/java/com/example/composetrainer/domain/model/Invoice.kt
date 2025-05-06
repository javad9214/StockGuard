package com.example.composetrainer.domain.model

data class Invoice(
    val id: Long,
    val prefix: String = "INV",
    val invoiceDate: String,
    val invoiceNumber: Long,

    val products: List<ProductWithQuantity>,
    val totalPrice: Long
)