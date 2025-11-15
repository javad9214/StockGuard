package com.example.composetrainer.domain.usecase.invoice

import android.util.Log
import com.example.composetrainer.domain.model.InvoiceId
import com.example.composetrainer.domain.model.InvoiceType
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.StockMovementFactory
import com.example.composetrainer.domain.model.recordSale
import com.example.composetrainer.domain.model.toEntity
import com.example.composetrainer.domain.model.updateInvoiceId
import com.example.composetrainer.domain.repository.InvoiceProductRepository
import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.repository.ProductRepository
import com.example.composetrainer.domain.repository.StockMovementRepository
import com.example.composetrainer.domain.usecase.product.DecreaseStockUseCase
import com.example.composetrainer.domain.usecase.product.IncreaseStockUseCase
import com.example.composetrainer.domain.usecase.sales.SaveProductSaleSummeryUseCase
import javax.inject.Inject

const val TAG = "InsertInvoiceUseCase"

class InsertInvoiceUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val stockMovementRepository: StockMovementRepository,
    private val productRepository: ProductRepository,
    private val invoiceProductRepository: InvoiceProductRepository,
    private val saveProductSaleSummeryUseCase: SaveProductSaleSummeryUseCase,
    private val increaseStockUseCase: IncreaseStockUseCase,
    private val decreaseStockUseCase: DecreaseStockUseCase
) {
    suspend operator fun invoke(invoiceWithProducts: InvoiceWithProducts) {

        // update invoice id to zero so the Room will create it automatically
        invoiceWithProducts.invoice.updateInvoiceId(InvoiceId(0))

        // save Invoice
        val invoiceId = invoiceRepository.createInvoice(invoiceWithProducts.invoice)

        // update invoiceId to all relatives
        invoiceWithProducts.updateInvoiceId(InvoiceId(invoiceId))

        // save InvoiceProduct
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {
                invoiceProductRepository.insertCrossRef(invoiceProduct)

            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "invoke: Error processing InvoiceProduct for item $index - ProductId: ${invoiceProduct.productId}",
                    e
                )
                throw e
            }
        }

        if (invoiceWithProducts.invoice.invoiceType == InvoiceType.SALE) {
            insertSaleInvoice(invoiceWithProducts)
        } else insertPurchaseInvoice(invoiceWithProducts)

    }

    private suspend fun insertSaleInvoice(invoiceWithProducts: InvoiceWithProducts) {

        // update product LastSaleDate
        invoiceWithProducts.products.forEach { product ->
            val updated = product.recordSale()

            Log.i(TAG, "before: ${product.lastSoldDate}")
            Log.i(TAG, "after: ${updated.lastSoldDate}")
            Log.i(TAG, "entity after: ${updated.toEntity().lastSoldDate}")

            val row = productRepository.updateProduct(updated)
            Log.i(TAG, "rows updated: $row")
        }


        // Decreasing Product Quantity
        invoiceWithProducts.products.forEachIndexed { index, product  ->
            try {
                decreaseStockUseCase.invoke(product, invoiceWithProducts.invoiceProducts[index].quantity.value)
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "invoke: Error processing DecreaseStockUseCase for item $index - ProductId: ${product.id}",
                    e
                )
                throw e
            }
        }

        // save ProductSalesSummary
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {

                saveProductSaleSummeryUseCase.invoke(invoiceProduct)

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

        // save StockMovement
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {
                stockMovementRepository.insert(
                    StockMovementFactory.createSale(
                        productId = invoiceProduct.productId.value,
                        quantitySold = invoiceProduct.quantity.value,
                        invoiceId = invoiceProduct.invoiceId.value,
                    )
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
    }

    private suspend fun insertPurchaseInvoice(invoiceWithProducts: InvoiceWithProducts) {

        // Increasing Product Quantity
        invoiceWithProducts.products.forEachIndexed { index, product  ->
            try {
                increaseStockUseCase.invoke(product, invoiceWithProducts.invoiceProducts[index].quantity.value)
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "invoke: Error processing IncreaseStockUseCase for item $index - ProductId: ${product.id}",
                    e
                )
                throw e
            }
        }

        // save StockMovement
        invoiceWithProducts.invoiceProducts.forEachIndexed { index, invoiceProduct ->
            try {
                stockMovementRepository.insert(
                    StockMovementFactory.createPurchase(
                        productId = invoiceProduct.productId.value,
                        quantityPurchased = invoiceProduct.quantity.value,
                        invoiceId = invoiceProduct.invoiceId.value,
                    )
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
    }
}