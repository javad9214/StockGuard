package com.example.composetrainer.domain.repository

import com.example.composetrainer.data.remote.dto.PagedResponseDto
import com.example.composetrainer.domain.model.Product
import com.example.login.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ServerMainProductRepository {
    suspend fun createProduct(product: Product): Flow<Resource<Long>>
    suspend fun updateProduct(id: Long, product: Product): Flow<Resource<String>>
    suspend fun deleteProduct(id: Long): Flow<Resource<String>>
    fun getAllProducts(page: Int, size: Int): Flow<Resource<PagedResponseDto<Product>>>
    fun searchProducts(query: String, page: Int, size: Int): Flow<Resource<PagedResponseDto<Product>>>
}