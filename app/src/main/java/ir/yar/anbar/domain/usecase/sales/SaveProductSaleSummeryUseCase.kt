package ir.yar.anbar.domain.usecase.sales

import ir.yar.anbar.domain.model.InvoiceProduct
import ir.yar.anbar.domain.model.ProductSalesSummaryFactory
import ir.yar.anbar.domain.model.SalesQuantity
import ir.yar.anbar.domain.model.toDomain
import ir.yar.anbar.domain.model.type.Money
import ir.yar.anbar.domain.repository.ProductSalesSummaryRepository
import ir.yar.anbar.utils.dateandtime.TimeStampUtil.getStartOfCurrentHour
import ir.yar.anbar.utils.dateandtime.TimeStampUtil.toDateTime
import javax.inject.Inject

class SaveProductSaleSummeryUseCase@Inject constructor(
    private val productSalesSummaryRepository: ProductSalesSummaryRepository
)  {
    suspend operator fun invoke( invoiceProduct : InvoiceProduct) {

        val TAG = "SaveProductSaleSummeryUseCase"

        val productId = invoiceProduct.productId.value
        val quantity = invoiceProduct.quantity.value
        val currentDate = getStartOfCurrentHour()
        val existingSummary = productSalesSummaryRepository.getByProductAndDate(productId, currentDate)

        if (existingSummary != null) {
            val updatedSummary = existingSummary.toDomain().copy(
                totalSold = SalesQuantity(existingSummary.totalSold + quantity),
                totalCost = Money(existingSummary.totalCost + invoiceProduct.calculateTotalCost().amount),
                totalRevenue = Money(existingSummary.totalRevenue + invoiceProduct.getTotalProfitAfterDiscount().amount)
            )
            productSalesSummaryRepository.updateProductSale(updatedSummary)
        } else {
            val newSummary = ProductSalesSummaryFactory.create(
                productId = productId,
                date = toDateTime(getStartOfCurrentHour()),
                totalSold = quantity,
                totalCost = invoiceProduct.calculateTotalCost().amount,
                totalRevenue = invoiceProduct.calculateTotalRevenue().amount
            )
            productSalesSummaryRepository.insertProductSale(newSummary)
        }
    }
}