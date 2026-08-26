package ir.yar.anbar.domain.usecase.invoice

import ir.yar.anbar.domain.model.InvoiceId
import ir.yar.anbar.domain.model.InvoiceNumber
import ir.yar.anbar.domain.model.InvoiceWithProducts
import javax.inject.Inject

class InitInvoiceWithProductsUseCase @Inject constructor(
    private val getInvoiceNumberUseCase: GetInvoiceNumberUseCase
) {
    suspend operator fun invoke(): InvoiceWithProducts {
        // One DB hit — the draft id is replaced by the real auto-generated id on insert
        val invoiceNumber = getInvoiceNumberUseCase.invoke()
        return InvoiceWithProducts.createDefault(
            invoiceId = InvoiceId(invoiceNumber),
            invoiceNumber = InvoiceNumber(invoiceNumber)
        )
    }
}
