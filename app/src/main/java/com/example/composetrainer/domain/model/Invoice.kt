package com.example.composetrainer.domain.model

data class Invoice(
    val id: Long,
    val numberId: Long,
    val dateTime: Long,
    val products: List<Product>,
    val totalPrice: Long
)