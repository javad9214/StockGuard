package com.example.composetrainer.domain.model

data class Invoice(
    val id: Long,
    val invoiceNumber: Int,
    val invoiceDate: String,
    val prefix: String = "INV",
    val products: List<ProductWithQuantity>,
    val totalPrice: Long
)