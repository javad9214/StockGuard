package com.example.composetrainer.domain.repository


import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.ProductWithQuantity
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {

    suspend fun createInvoice(products: List<ProductWithQuantity>)

    suspend fun getInvoiceWithProducts(invoiceId: Long): InvoiceWithProducts

    suspend fun getAllInvoices(): Flow<List<InvoiceWithProducts>>

    suspend fun deleteInvoice(invoiceId: Long)

    suspend fun getNextInvoiceNumberId(): Long
}