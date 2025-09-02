package com.example.composetrainer.domain.usecase.invoice

import android.util.Log
import com.example.composetrainer.domain.model.InvoiceId
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.StockMovementFactory
import com.example.composetrainer.domain.model.updateInvoiceId
import com.example.composetrainer.domain.repository.InvoiceProductRepository
import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.repository.StockMovementRepository
import com.example.composetrainer.domain.usecase.sales.SaveProductSaleSummeryUseCase
import javax.inject.Inject

const val TAG = "InsertInvoiceUseCase"

class InsertInvoiceUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val stockMovementRepository: StockMovementRepository,
    private val invoiceProductRepository: InvoiceProductRepository,
    private val saveProductSaleSummeryUseCase: SaveProductSaleSummeryUseCase
) {
    suspend operator fun invoke(invoiceWithProducts: InvoiceWithProducts) {

        // Log initial state
        Log.i(TAG, "invoke: Starting InsertInvoiceUseCase")
        Log.i(TAG, "invoke: Invoice ID: ${invoiceWithProducts.invoice.id}")
        Log.i(TAG, "invoke: InvoiceProducts count: ${invoiceWithProducts.invoiceProducts.size}")

        // Log each product in the initial list
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, product ->
            Log.i(
                TAG,
                "invoke: Initial Product $index - ID: ${product.productId}, Quantity: ${product.quantity}, InvoiceId: ${product.invoiceId}"
            )
        }

        // update invoice id to zero so the Room will create it automatically
        invoiceWithProducts.invoice.updateInvoiceId(InvoiceId(0))

        // save Invoice
        val invoiceId = invoiceRepository.createInvoice(invoiceWithProducts.invoice)
        Log.i(TAG, "invoke: Created invoice with ID: $invoiceId")

        // update invoiceId to all relatives
        invoiceWithProducts.updateInvoiceId(InvoiceId(invoiceId))
        Log.i(TAG, "invoke: Updated all products with new invoice ID: $invoiceId")

        // Log updated products
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, product ->
            Log.i(
                TAG,
                "invoke: Updated Product $index - ID: ${product.productId}, Quantity: ${product.quantity}, InvoiceId: ${product.invoiceId}"
            )
        }

        // save InvoiceProduct
        Log.i(
            TAG,
            "invoke: Starting InvoiceProduct loop with ${invoiceWithProducts.invoiceProducts.size} items"
        )
        Log.i(TAG, "invoke: InvoiceProducts list content before processing:")
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, product ->
            Log.i(
                TAG,
                "invoke: List item $index - ProductId: ${product.productId}, Quantity: ${product.quantity}"
            )
        }

        var invoiceProductProcessedCount = 0
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {
                invoiceProductProcessedCount++
                Log.i(
                    TAG,
                    "invoke: Processing InvoiceProduct $invoiceProductProcessedCount/${invoiceWithProducts.invoiceProducts.size} - item $index - ProductId: ${invoiceProduct.productId}"
                )
                invoiceProductRepository.insertCrossRef(invoiceProduct)
                Log.i(
                    TAG,
                    "invoke: Successfully processed InvoiceProduct $invoiceProductProcessedCount/${invoiceWithProducts.invoiceProducts.size} - item $index - ProductId: ${invoiceProduct.productId}"
                )
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "invoke: Error processing InvoiceProduct for item $index - ProductId: ${invoiceProduct.productId}",
                    e
                )
                throw e
            }
        }
        Log.i(
            TAG,
            "invoke: Completed InvoiceProduct loop - Processed $invoiceProductProcessedCount items"
        )

        // save ProductSalesSummary
        Log.i(
            TAG,
            "invoke: Starting ProductSalesSummary loop with ${invoiceWithProducts.invoiceProducts.size} items"
        )
        Log.i(
            TAG,
            "invoke: Verifying InvoiceProducts list size before ProductSalesSummary: ${invoiceWithProducts.invoiceProducts.size}"
        )

        var salesSummaryProcessedCount = 0
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {
                salesSummaryProcessedCount++
                Log.i(
                    TAG,
                    "invoke: Processing ProductSalesSummary $salesSummaryProcessedCount/${invoiceWithProducts.invoiceProducts.size} - item $index - ProductId: ${invoiceProduct.productId}"
                )
                saveProductSaleSummeryUseCase.invoke(invoiceProduct)
                Log.i(
                    TAG,
                    "invoke: Successfully processed ProductSalesSummary $salesSummaryProcessedCount/${invoiceWithProducts.invoiceProducts.size} - item $index - ProductId: ${invoiceProduct.productId}"
                )
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "invoke: Error processing ProductSalesSummary for item $index - ProductId: ${invoiceProduct.productId}",
                    e
                )
                // Re-throw to maintain original behavior, but with better logging
                throw e
            }
        }
        Log.i(
            TAG,
            "invoke: Completed ProductSalesSummary loop - Processed $salesSummaryProcessedCount items"
        )

        // save StockMovement
        Log.i(
            TAG,
            "invoke: Starting StockMovement loop with ${invoiceWithProducts.invoiceProducts.size} items"
        )
        Log.i(
            TAG,
            "invoke: Verifying InvoiceProducts list size before StockMovement: ${invoiceWithProducts.invoiceProducts.size}"
        )

        var stockMovementProcessedCount = 0
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {
                stockMovementProcessedCount++
                Log.i(
                    TAG,
                    "invoke: Processing StockMovement $stockMovementProcessedCount/${invoiceWithProducts.invoiceProducts.size} - item $index - ProductId: ${invoiceProduct.productId}, Quantity: ${invoiceProduct.quantity}"
                )
                stockMovementRepository.insert(
                    StockMovementFactory.createSale(
                        productId = invoiceProduct.productId.value,
                        quantitySold = invoiceProduct.quantity.value,
                        invoiceId = invoiceProduct.invoiceId.value,
                    )
                )
                Log.i(
                    TAG,
                    "invoke: Successfully processed StockMovement $stockMovementProcessedCount/${invoiceWithProducts.invoiceProducts.size} - item $index - ProductId: ${invoiceProduct.productId}"
                )
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "invoke: Error processing StockMovement for item $index - ProductId: ${invoiceProduct.productId}",
                    e
                )
                throw e
            }
        }
        Log.i(
            TAG,
            "invoke: Completed StockMovement loop - Processed $stockMovementProcessedCount items"
        )

        Log.i(TAG, "invoke: InsertInvoiceUseCase completed successfully")
        Log.i(
            TAG,
            "invoke: Final summary - InvoiceProducts: $invoiceProductProcessedCount, SalesSummary: $salesSummaryProcessedCount, StockMovements: $stockMovementProcessedCount"
        )
    }
}