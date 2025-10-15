package com.example.composetrainer.domain.usecase.sales

import android.util.Log
import com.example.composetrainer.domain.model.InvoiceProduct
import com.example.composetrainer.domain.model.ProductSalesSummaryFactory
import com.example.composetrainer.domain.model.SalesQuantity
import com.example.composetrainer.domain.model.toDomain
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getStartOfCurrentHour
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.toDateTime
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
                totalCost = Money(existingSummary.totalCost + invoiceProduct.calculateTotal().amount),
                totalRevenue = Money(existingSummary.totalRevenue + invoiceProduct.getTotalProfitAfterDiscount().amount)
            )
            Log.i(TAG, "invoke:  SaveProductSaleSummeryUseCase ")
            productSalesSummaryRepository.updateProductSale(updatedSummary)
        } else {
            val newSummary = ProductSalesSummaryFactory.create(
                productId = productId,
                date = toDateTime(getStartOfCurrentHour()),
                totalSold = quantity,
                totalCost = invoiceProduct.calculateTotal().amount,
                totalRevenue = invoiceProduct.calculateTotal().amount
            )
            productSalesSummaryRepository.insertProductSale(newSummary)
        }
    }
}