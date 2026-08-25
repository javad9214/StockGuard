package ir.yar.anbar.domain.usecase.sales

import ir.yar.anbar.domain.model.InvoiceProduct
import ir.yar.anbar.domain.model.ProductSalesSummaryFactory
import ir.yar.anbar.domain.model.SalesQuantity
import ir.yar.anbar.domain.model.type.Money
import ir.yar.anbar.domain.repository.ProductSalesSummaryRepository
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class SaveProductSaleSummeryUseCase @Inject constructor(
    private val productSalesSummaryRepository: ProductSalesSummaryRepository
) {
    suspend operator fun invoke(invoiceProduct: InvoiceProduct) {
        val productId = invoiceProduct.productId.value
        val quantity = invoiceProduct.quantity.value

        // The date column stores local midnight, so summaries aggregate per day;
        // looking up by any other key (e.g. start of hour) would miss the
        // existing row and fragment the day into duplicate inserts
        val today = LocalDate.now()
        val dayKey = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val existingSummary =
            productSalesSummaryRepository.getByProductAndDate(productId, dayKey)

        if (existingSummary != null) {
            val updatedSummary = existingSummary.copy(
                totalSold = SalesQuantity(existingSummary.totalSold.value + quantity),
                totalCost = Money(existingSummary.totalCost.amount + invoiceProduct.calculateTotalCost().amount),
                totalRevenue = Money(existingSummary.totalRevenue.amount + invoiceProduct.calculateTotalRevenue().amount)
            )
            productSalesSummaryRepository.updateProductSale(updatedSummary)
        } else {
            val newSummary = ProductSalesSummaryFactory.create(
                productId = productId,
                date = today,
                totalSold = quantity,
                totalCost = invoiceProduct.calculateTotalCost().amount,
                totalRevenue = invoiceProduct.calculateTotalRevenue().amount
            )
            productSalesSummaryRepository.insertProductSale(newSummary)
        }
    }
}
