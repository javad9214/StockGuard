package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.InvoiceProduct

interface InvoiceProductRepository {

    /**
     * Persists one invoice line. Failures propagate — callers such as
     * InsertInvoiceUseCase log and rethrow so a partial save is surfaced
     * instead of silently dropping lines.
     */
    suspend fun insertCrossRef(invoiceProduct: InvoiceProduct)
}
