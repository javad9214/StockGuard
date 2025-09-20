package com.example.composetrainer.domain.repository

import com.example.composetrainer.data.remote.dto.PagedResponseDto
import com.example.composetrainer.data.remote.dto.ProductDto
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ServerMainProductRepository {
    suspend fun createProduct(product: ProductDto): ProductDto
    suspend fun updateProduct(id: Long, product: ProductDto): ProductDto
    suspend fun deleteProduct(id: Long)
    fun getAllProducts(page: Int, size: Int): Flow<Resource<PagedResponseDto<Product>>>
    fun searchProducts(query: String, page: Int, size: Int): Flow<Resource<PagedResponseDto<Product>>>
}