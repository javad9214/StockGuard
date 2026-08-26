package ir.yar.anbar.domain.usecase.invoice

import ir.yar.anbar.domain.model.InvoiceSyncResult
import ir.yar.anbar.domain.repository.InvoiceRepository
import javax.inject.Inject

/**
 * Pushes every locally pending invoice change (creates, deletes) to the
 * server, then pulls server-side changes back, and reports the counts.
 */
class SyncInvoicesUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {
    suspend operator fun invoke(): InvoiceSyncResult {
        return invoiceRepository.syncInvoices()
    }
}