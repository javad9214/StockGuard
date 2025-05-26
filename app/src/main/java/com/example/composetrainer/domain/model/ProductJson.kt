package com.example.composetrainer.domain.model

//TODO Remove this data Class in Production

data class ProductJson(
    val name: String,
    val barcode: String,
    val category: String?,
    val subcategory: String?
)
