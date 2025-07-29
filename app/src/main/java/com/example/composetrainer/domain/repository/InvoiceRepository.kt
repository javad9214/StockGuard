package com.example.composetrainer.domain.repository



import com.example.composetrainer.domain.model.InvoiceProduct
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.domain.model.TopSellingProductInfo
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {

    suspend fun createInvoice(products: List<ProductWithQuantity>)

    suspend fun getInvoiceWithProducts(invoiceId: Long): Flow<InvoiceWithProducts>

    suspend fun getAllInvoices(): Flow<List<InvoiceWithProducts>>

    suspend fun getAllInvoicesOldestFirst(): Flow<List<InvoiceWithProducts>>

    suspend fun deleteInvoice(invoiceId: Long)

    suspend fun getNextInvoiceNumberId(): Long

    // Analytics methods
    suspend fun getTotalSalesForMonth(yearMonth: String): Long

    suspend fun getTotalInvoicesForMonth(yearMonth: String): Int

    suspend fun getTotalQuantityForMonth(yearMonth: String): Int

    suspend fun getTopSellingProductsForMonth(yearMonth: String): List<TopSellingProductInfo>

    suspend fun getTotalInvoicesBetweenDates(start: Long, end: Long): Int  
    
    // Debug methods
    suspend fun getTotalInvoiceCount(): Int
    suspend fun getRecentInvoicesForDebug(): List<String> // Return just the dates as strings
}