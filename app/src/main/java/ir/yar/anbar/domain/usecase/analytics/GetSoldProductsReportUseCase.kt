package ir.yar.anbar.domain.usecase.analytics

import ir.yar.anbar.domain.repository.InvoiceRepository
import javax.inject.Inject

class GetSoldProductsReportUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) {

}