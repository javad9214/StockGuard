package ir.yar.anbar.domain.repository



import ir.yar.anbar.domain.model.Invoice
import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.model.TopSellingProductInfo
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {

    suspend fun createInvoice(invoice : Invoice): Long

    /**
     * Get a single invoice and its products by invoice ID. The returned InvoiceWithProducts
     * includes both itemized invoice products and the corresponding full product details (products property).
     */
    fun getInvoiceWithProducts(invoiceId: Long): Flow<InvoiceWithProducts>

    /**
     * Get all invoices, each including both invoice product lines and the corresponding
     * full Product domain objects for each product in the invoice (products property).
     */
    fun getAllInvoices(): Flow<List<InvoiceWithProducts>>

    /**
     * Like getAllInvoices, but returns invoices sorted with oldest first. Each invoice includes full item and product details.
     */
    fun getAllInvoicesOldestFirst(): Flow<List<InvoiceWithProducts>>

    suspend fun deleteInvoice(invoiceId: Long)

    suspend fun getNextInvoiceNumberId(): Long

    // Analytics methods
    suspend fun getTotalSalesForMonth(yearMonth: String): Long

    suspend fun getTotalInvoicesForMonth(yearMonth: String): Int

    suspend fun getTotalQuantityForMonth(yearMonth: String): Int

    suspend fun getTopSellingProductsForMonth(yearMonth: String): List<TopSellingProductInfo>

    fun getTotalProfitBetweenDates(start: Long, end: Long): Flow<Long>

    fun getTotalSalesBetweenDates(start: Long, end: Long): Flow<Long>

    fun getTotalInvoicesBetweenDates(start: Long, end: Long): Flow<Int>
    
    // Debug methods
    suspend fun getTotalInvoiceCount(): Int
    suspend fun getRecentInvoicesForDebug(): List<String> // Return just the dates as strings
}