package ir.yar.anbar.domain.usecase.invoice

import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInvoiceWithDetailsUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {
    operator fun invoke(invoiceId: Long): Flow<InvoiceWithProducts> {
        return invoiceRepository.getInvoiceWithProducts(invoiceId)
    }
}