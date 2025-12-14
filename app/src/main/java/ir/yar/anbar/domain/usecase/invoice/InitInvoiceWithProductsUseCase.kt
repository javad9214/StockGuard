package ir.yar.anbar.domain.usecase.invoice

import android.util.Log
import ir.yar.anbar.domain.model.InvoiceId
import ir.yar.anbar.domain.model.InvoiceNumber
import ir.yar.anbar.domain.model.InvoiceWithProducts
import javax.inject.Inject


class InitInvoiceWithProductsUseCase @Inject constructor(
    private val getInvoiceNumberUseCase: GetInvoiceNumberUseCase
) {

    suspend operator fun invoke(): InvoiceWithProducts {
        Log.i("InitInvoiceWithProductsUseCase", "invoke: InitInvoiceWithProductsUseCase ")
        return InvoiceWithProducts.createDefault(
            invoiceId = InvoiceId(getInvoiceNumberUseCase.invoke()), // Change to Real InvoiceId later on Insert To DB
            invoiceNumber = InvoiceNumber(
                getInvoiceNumberUseCase.invoke()
            )
        )
    }
}