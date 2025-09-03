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

        // update invoice id to zero so the Room will create it automatically
        invoiceWithProducts.invoice.updateInvoiceId(InvoiceId(0))

        // save Invoice
        val invoiceId = invoiceRepository.createInvoice(invoiceWithProducts.invoice)

        // update invoiceId to all relatives
        invoiceWithProducts.updateInvoiceId(InvoiceId(invoiceId))

        // save InvoiceProduct
        Log.i(
            TAG,
            "invoke: Starting InvoiceProduct loop with ${invoiceWithProducts.invoiceProducts.size} items"
        )
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {
                Log.i(
                    TAG,
                    "invoke: Processing InvoiceProduct for item $index - ProductId: ${invoiceProduct.productId}"
                )
                invoiceProductRepository.insertCrossRef(invoiceProduct)
                Log.i(
                    TAG,
                    "invoke: Successfully processed InvoiceProduct for item $index - ProductId: ${invoiceProduct.productId}"
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
        Log.i(TAG, "invoke: Completed InvoiceProduct loop")

        // save ProductSalesSummary
        Log.i(
            TAG,
            "invoke: Starting ProductSalesSummary loop with ${invoiceWithProducts.invoiceProducts.size} items"
        )
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {
                Log.i(
                    TAG,
                    "invoke: Processing item $index - ProductId: ${invoiceProduct.productId}"
                )
                saveProductSaleSummeryUseCase.invoke(invoiceProduct)
                Log.i(
                    TAG,
                    "invoke: Successfully processed item $index - ProductId: ${invoiceProduct.productId}"
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
        Log.i(TAG, "invoke: Completed ProductSalesSummary loop")

        // save StockMovement
        Log.i(
            TAG,
            "invoke: Starting StockMovement loop with ${invoiceWithProducts.invoiceProducts.size} items"
        )
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {
                Log.i(
                    TAG,
                    "invoke: Processing StockMovement for item $index - ProductId: ${invoiceProduct.productId}"
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
                    "invoke: Successfully processed StockMovement for item $index - ProductId: ${invoiceProduct.productId}"
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
        Log.i(TAG, "invoke: Completed StockMovement loop")
    }
}