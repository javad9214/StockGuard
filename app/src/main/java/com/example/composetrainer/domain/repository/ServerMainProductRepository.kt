package com.example.composetrainer.domain.repository

import com.example.composetrainer.data.remote.dto.PagedResponseDto
import com.example.composetrainer.data.remote.dto.ProductDto
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.util.Resource

interface ServerMainProductRepository {
    suspend fun createProduct(product: ProductDto): ProductDto
    suspend fun updateProduct(id: Long, product: ProductDto): ProductDto
    suspend fun deleteProduct(id: Long)
    suspend fun getAllProducts(page: Int, size: Int): Resource<PagedResponseDto<Product>>
    suspend fun searchProducts(query: String, page: Int, size: Int): Resource<PagedResponseDto<Product>>
}