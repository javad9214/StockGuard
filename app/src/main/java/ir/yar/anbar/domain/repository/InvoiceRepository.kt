package ir.yar.anbar.domain.repository


import ir.yar.anbar.domain.model.Invoice
import ir.yar.anbar.domain.model.InvoiceSyncResult
import ir.yar.anbar.domain.model.InvoiceWithProducts
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {

    suspend fun createInvoice(invoice: Invoice): Long

    /**
     * Pushes every locally pending invoice change (creates and deletes) to the
     * server, then pulls server-side changes back into the local DB.
     */
    suspend fun syncInvoices(): InvoiceSyncResult

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

    fun getTotalProfitBetweenDates(start: Long, end: Long): Flow<Long>

    fun getTotalSalesBetweenDates(start: Long, end: Long): Flow<Long>

    fun getTotalInvoicesBetweenDates(start: Long, end: Long): Flow<Int>
}
