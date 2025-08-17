package com.example.composetrainer.domain.usecase.invoice

import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllInvoiceUseCase  @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {
    operator fun invoke(): Flow<List<InvoiceWithProducts>> {
        return invoiceRepository.getAllInvoices()
    }
}