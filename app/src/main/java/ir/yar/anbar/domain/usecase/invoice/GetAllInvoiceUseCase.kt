package ir.yar.anbar.domain.usecase.invoice

import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllInvoiceUseCase  @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {
    operator fun invoke(): Flow<List<InvoiceWithProducts>> {
        return invoiceRepository.getAllInvoices()
    }
}