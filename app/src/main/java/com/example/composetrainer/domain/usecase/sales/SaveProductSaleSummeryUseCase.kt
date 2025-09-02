package com.example.composetrainer.domain.usecase.sales

import android.util.Log
import com.example.composetrainer.domain.model.InvoiceProduct
import com.example.composetrainer.domain.model.ProductSalesSummaryFactory
import com.example.composetrainer.domain.model.type.Money
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.getStartOfCurrentHour
import com.example.composetrainer.utils.dateandtime.TimeStampUtil.toDateTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class SaveProductSaleSummeryUseCase@Inject constructor(
    private val productSalesSummaryRepository: ProductSalesSummaryRepository
)  {
    suspend operator fun invoke( invoiceProduct : InvoiceProduct) {

        val TAG = "SaveProductSaleSummeryUseCase"

        val productId = invoiceProduct.productId.value
        val quantity = invoiceProduct.quantity.value
        val currentDate = getStartOfCurrentHour()

        // Log input data
        Log.i(TAG, "invoke: Processing ProductSalesSummary for ProductId: $productId")
        Log.i(TAG, "invoke: Input data - Quantity: $quantity")
        Log.i(TAG, "invoke: Input data - PriceAtSale: ${invoiceProduct.priceAtSale.amount}")
        Log.i(
            TAG,
            "invoke: Input data - CostPriceAtTransaction: ${invoiceProduct.costPriceAtTransaction.amount}"
        )
        Log.i(TAG, "invoke: Input data - Discount: ${invoiceProduct.discount.amount}")
        Log.i(TAG, "invoke: Input data - Total: ${invoiceProduct.total.amount}")

        // Calculate values correctly
        val totalSalesAmount =
            invoiceProduct.calculateTotal() // What customer paid (after discount)
        val totalCostOfGoods =
            Money(invoiceProduct.costPriceAtTransaction.amount * quantity) // Cost of goods sold
        val totalProfit = invoiceProduct.getTotalProfitAfterDiscount() // Profit earned

        Log.i(TAG, "invoke: Calculated Total Sales Amount: ${totalSalesAmount.amount}")
        Log.i(TAG, "invoke: Calculated Total Cost of Goods: ${totalCostOfGoods.amount}")
        Log.i(TAG, "invoke: Calculated Total Profit: ${totalProfit.amount}")
        Log.i(TAG, "invoke: Current Date: $currentDate")

        try {
            // Use withTimeout to prevent long-running operations and NonCancellable to ensure critical database operations complete
            withTimeout(10000L) { // 10 second timeout
                withContext(NonCancellable) {
                    Log.i(TAG, "invoke: Creating ProductSalesSummary for upsert")

                    // Create a summary with the values to be added/accumulated
                    val summaryToUpsert = ProductSalesSummaryFactory.create(
                        productId = productId,
                        date = toDateTime(getStartOfCurrentHour()),
                        totalSold = quantity,
                        totalRevenue = totalSalesAmount.amount, // Total sales amount (what customer paid)
                        totalCost = totalCostOfGoods.amount // Total cost of goods sold
                    )

                    Log.i(
                        TAG,
                        "invoke: Upserting ProductSalesSummary - TotalSold: $quantity, TotalRevenue: ${totalSalesAmount.amount}, TotalCost: ${totalCostOfGoods.amount}"
                    )

                    // Use upsert method which handles insert/update logic in the DAO
                    productSalesSummaryRepository.upsertProductSale(summaryToUpsert)

                    Log.i(
                        TAG,
                        "invoke: Successfully upserted ProductSalesSummary for ProductId: $productId"
                    )
                }
            }
        } catch (cancellation: CancellationException) {
            Log.w(TAG, "invoke: Operation was cancelled for ProductId: $productId")
            // Try to complete the operation in NonCancellable context as a fallback
            try {
                withContext(NonCancellable) {
                    Log.i(
                        TAG,
                        "invoke: Attempting to complete upsert operation in NonCancellable context"
                    )

                    val summaryToUpsert = ProductSalesSummaryFactory.create(
                        productId = productId,
                        date = toDateTime(getStartOfCurrentHour()),
                        totalSold = quantity,
                        totalRevenue = totalSalesAmount.amount,
                        totalCost = totalCostOfGoods.amount
                    )

                    productSalesSummaryRepository.upsertProductSale(summaryToUpsert)
                    Log.i(
                        TAG,
                        "invoke: Successfully completed upsert operation despite cancellation"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "invoke: Failed to complete upsert operation even in NonCancellable context",
                    e
                )
                // Don't re-throw cancellation exceptions as they're expected during scope cancellation
                if (e !is CancellationException) {
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "invoke: Error in SaveProductSaleSummeryUseCase", e)
            throw e
        }

        Log.i(TAG, "invoke: Completed SaveProductSaleSummeryUseCase for ProductId: $productId")
    }
}