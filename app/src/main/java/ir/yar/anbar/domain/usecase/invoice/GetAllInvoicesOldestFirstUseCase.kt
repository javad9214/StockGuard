package ir.yar.anbar.domain.usecase.invoice

import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllInvoicesOldestFirstUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    operator fun invoke(): Flow<List<InvoiceWithProducts>> {
        return repository.getAllInvoicesOldestFirst()
    }
}
