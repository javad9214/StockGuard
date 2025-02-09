package com.example.composetrainer.domain.repository

import com.example.composetrainer.data.relation.ProductWithQuantity
import com.example.composetrainer.domain.model.Invoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    suspend fun createInvoice(products: List<ProductWithQuantity>): Invoice

    fun getAllInvoices(): Flow<List<Invoice>>

    suspend fun deleteInvoice(invoiceId: Long)
}