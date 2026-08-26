package ir.yar.anbar.domain.usecase.invoice

import ir.yar.anbar.domain.model.InvoiceId
import ir.yar.anbar.domain.model.InvoiceType
import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.model.StockMovementFactory
import ir.yar.anbar.domain.model.recordSale
import ir.yar.anbar.domain.model.updateInvoiceId
import ir.yar.anbar.domain.repository.InvoiceProductRepository
import ir.yar.anbar.domain.repository.InvoiceRepository
import ir.yar.anbar.domain.repository.ProductRepository
import ir.yar.anbar.domain.repository.StockMovementRepository
import ir.yar.anbar.domain.usecase.product.DecreaseStockUseCase
import ir.yar.anbar.domain.usecase.product.IncreaseStockUseCase
import ir.yar.anbar.domain.usecase.sales.SaveProductSaleSummeryUseCase
import javax.inject.Inject

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
        // The draft carries a preview id; zero it so Room auto-generates the
        // real primary key on insert instead of forcing the preview value
        val draft = invoiceWithProducts.updateInvoiceId(InvoiceId(0))
        val invoiceId = invoiceRepository.createInvoice(draft.invoice)

        // Re-point every invoice line at the generated id before writing them,
        // so cross-refs and stock movements reference the persisted invoice
        val invoice = invoiceWithProducts.updateInvoiceId(InvoiceId(invoiceId))

        // Save invoice lines; failures propagate to the caller (ViewModel)
        invoice.invoiceProducts.forEach { invoiceProduct ->
            invoiceProductRepository.insertCrossRef(invoiceProduct)
        }

        if (invoice.invoice.invoiceType == InvoiceType.SALE) {
            insertSaleInvoice(invoice)
        } else {
            insertPurchaseInvoice(invoice)
        }
    }

    private suspend fun insertSaleInvoice(invoiceWithProducts: InvoiceWithProducts) {
        // Update product LastSaleDate
        val updatedProducts = invoiceWithProducts.products.map { product ->
            product.recordSale()
        }

        // Save the updated products with lastSoldDate
        updatedProducts.forEach { updated ->
            productRepository.updateProduct(updated)
        }

        // Decrease stock using the UPDATED products
        updatedProducts.forEachIndexed { index, updatedProduct ->
            decreaseStockUseCase.invoke(
                updatedProduct,
                invoiceWithProducts.invoiceProducts[index].quantity.value
            )
        }

        // Save ProductSalesSummary
        invoiceWithProducts.invoiceProducts.forEach { invoiceProduct ->
            saveProductSaleSummeryUseCase.invoke(invoiceProduct)
        }

        // Save StockMovement
        invoiceWithProducts.invoiceProducts.forEach { invoiceProduct ->
            stockMovementRepository.insert(
                StockMovementFactory.createSale(
                    productId = invoiceProduct.productId.value,
                    quantitySold = invoiceProduct.quantity.value,
                    invoiceId = invoiceProduct.invoiceId.value,
                )
            )
        }
    }

    private suspend fun insertPurchaseInvoice(invoiceWithProducts: InvoiceWithProducts) {
        // Increase stock
        invoiceWithProducts.products.forEachIndexed { index, product ->
            increaseStockUseCase.invoke(
                product,
                invoiceWithProducts.invoiceProducts[index].quantity.value
            )
        }

        // Save StockMovement
        invoiceWithProducts.invoiceProducts.forEach { invoiceProduct ->
            stockMovementRepository.insert(
                StockMovementFactory.createPurchase(
                    productId = invoiceProduct.productId.value,
                    quantityPurchased = invoiceProduct.quantity.value,
                    invoiceId = invoiceProduct.invoiceId.value,
                )
            )
        }
    }
}
