package ir.yar.anbar.domain.usecase.invoice

import ir.yar.anbar.domain.repository.InvoiceRepository
import javax.inject.Inject

class DeleteInvoiceUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {
    suspend operator fun invoke(invoiceId: Long) {
        invoiceRepository.deleteInvoice(invoiceId)
    }

    suspend fun deleteMultiple(invoiceIds: List<Long>) {
        invoiceIds.forEach { invoiceId ->
            invoiceRepository.deleteInvoice(invoiceId)
        }
    }
}