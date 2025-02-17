package com.example.composetrainer.domain.repository


import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.model.ProductWithQuantity
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    suspend fun createInvoice(products: List<ProductWithQuantity>): Invoice

    fun getAllInvoices(): Flow<List<Invoice>>

    suspend fun deleteInvoice(invoiceId: Long)
}