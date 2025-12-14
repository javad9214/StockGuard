package ir.yar.anbar.domain.usecase.invoice.purchase

import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.repository.InvoiceProductRepository
import ir.yar.anbar.domain.repository.InvoiceRepository
import ir.yar.anbar.domain.repository.StockMovementRepository
import ir.yar.anbar.domain.usecase.sales.SaveProductSaleSummeryUseCase
import javax.inject.Inject

class InsertPurchaseInvoiceUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val stockMovementRepository: StockMovementRepository,
    private val invoiceProductRepository: InvoiceProductRepository,
    private val saveProductSaleSummeryUseCase: SaveProductSaleSummeryUseCase
) {
    suspend operator fun invoke(invoiceWithProducts: InvoiceWithProducts) {

    }
}