package ir.yar.anbar.domain.usecase.invoice

import ir.yar.anbar.domain.repository.InvoiceRepository
import javax.inject.Inject

class GetInvoiceNumberUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(): Long {
        return repository.getNextInvoiceNumberId()
    }
}
