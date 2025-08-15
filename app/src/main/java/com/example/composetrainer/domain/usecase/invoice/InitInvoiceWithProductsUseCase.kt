package com.example.composetrainer.domain.usecase.invoice

import com.example.composetrainer.domain.model.InvoiceFactory
import com.example.composetrainer.domain.model.InvoiceId
import com.example.composetrainer.domain.model.InvoiceNumber
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.repository.InvoiceRepository
import javax.inject.Inject

class InitInvoiceWithProductsUseCase @Inject constructor(
    private val getInvoiceNumberUseCase: GetInvoiceNumberUseCase,
    private val invoiceRepository: InvoiceRepository
) {

    suspend operator fun invoke(): InvoiceWithProducts {
        return InvoiceWithProducts.createDefault(
            invoiceId = InvoiceId(invoiceRepository.createInvoice(InvoiceFactory.createDraft())),
            invoiceNumber = InvoiceNumber(
                getInvoiceNumberUseCase.invoke()
            )
        )
    }
}