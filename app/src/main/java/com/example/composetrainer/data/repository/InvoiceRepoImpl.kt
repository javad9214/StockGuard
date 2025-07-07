package com.example.composetrainer.data.repository

import com.example.composetrainer.data.local.dao.InvoiceDao
import com.example.composetrainer.data.local.dao.InvoiceProductDao
import com.example.composetrainer.data.local.dao.ProductDao
import com.example.composetrainer.data.local.dao.TopSellingProduct
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.entity.InvoiceProductCrossRef
import com.example.composetrainer.data.local.relation.InvoiceWithProduct
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.domain.model.TopSellingProductInfo
import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.usecase.sales.AddToProductSalesSummaryUseCase
import com.example.composetrainer.utils.FarsiDateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class InvoiceRepoImpl @Inject constructor(
    val invoiceDao: InvoiceDao,
    private val invoiceProductDao: InvoiceProductDao,
    private val productDao: ProductDao,
    private val addToProductSalesSummaryUseCase: AddToProductSalesSummaryUseCase
) : InvoiceRepository {

    override suspend fun createInvoice(products: List<ProductWithQuantity>) {
        val invoiceNumber = getNextInvoiceNumberId()

        val invoice = InvoiceEntity(
            invoiceNumber = invoiceNumber,
            invoiceDate = FarsiDateUtil.getTodayAsTimestamp(),
            createdAt = System.currentTimeMillis()
        )

        val invoiceId = invoiceDao.insertInvoice(invoice)

        products.forEach { productWithQuantity ->
            val product = productWithQuantity.product
            val invoiceProductCrossRef = InvoiceProductCrossRef(
                invoiceId = invoiceId,
                productId = productWithQuantity.product.id,
                quantity = productWithQuantity.quantity,
                priceAtSale = product.price ?: 0L
            )
            invoiceProductDao.insertCrossRef(invoiceProductCrossRef)
        }

        // Update product sales summary
        addToProductSalesSummaryUseCase(products)
    }

    override suspend fun getInvoiceWithProducts(invoiceId: Long): InvoiceWithProducts {
        val invoiceWithProducts = invoiceDao.getInvoiceWithProducts(invoiceId)
        return mapToInvoiceWithProducts(invoiceWithProducts)
    }

    override suspend fun getAllInvoices(): Flow<List<InvoiceWithProducts>> {
        return invoiceDao.getAllInvoiceWithProducts().map { list ->
            val groupedByInvoiceId = list.groupBy { it.invoiceId }
            groupedByInvoiceId.map { (_, invoiceProducts) ->
                mapToInvoiceWithProducts(invoiceProducts)
            }
        }
    }

    override suspend fun getAllInvoicesOldestFirst(): Flow<List<InvoiceWithProducts>> {
        return invoiceDao.getAllInvoiceWithProductsOldestFirst().map { list ->
            val groupedByInvoiceId = list.groupBy { it.invoiceId }
            groupedByInvoiceId.map { (_, invoiceProducts) ->
                mapToInvoiceWithProducts(invoiceProducts)
            }
        }
    }

    override suspend fun deleteInvoice(invoiceId: Long) {
        invoiceDao.deleteInvoice(invoiceId)
    }

    override suspend fun getNextInvoiceNumberId(): Long {
        val lastInvoice = invoiceDao.getLastInvoice()
        return if (lastInvoice != null) {
            lastInvoice.invoiceNumber + 1
        } else {
            1000 // Start from 1000 if no invoices exist
        }
    }

    // Analytics methods
    override suspend fun getTotalSalesForMonth(yearMonth: String): Long {
        return invoiceDao.getTotalSalesForMonth(yearMonth)
    }

    override suspend fun getTotalInvoicesForMonth(yearMonth: String): Int {
        return invoiceDao.getTotalInvoicesForMonth(yearMonth)
    }

    override suspend fun getTotalQuantityForMonth(yearMonth: String): Int {
        return invoiceDao.getTotalQuantityForMonth(yearMonth)
    }

    override suspend fun getTopSellingProductsForMonth(yearMonth: String): List<TopSellingProductInfo> {
        return invoiceDao.getTopSellingProductsForMonth(yearMonth).map {
            TopSellingProductInfo(
                name = it.name,
                totalQuantity = it.totalQuantity,
                totalSales = it.totalSales
            )
        }
    }

    // Debug methods
    override suspend fun getTotalInvoiceCount(): Int {
        return invoiceDao.getTotalInvoiceCount()
    }

    override suspend fun getRecentInvoicesForDebug(): List<String> {
        return invoiceDao.getRecentInvoicesForDebug().map {
            val date = Date(it.invoiceDate)
            SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date)
        }
    }

    // Helper methods
    private fun mapToInvoiceWithProducts(invoiceWithProducts: List<InvoiceWithProduct>): InvoiceWithProducts {
        if (invoiceWithProducts.isEmpty()) {
            return InvoiceWithProducts(
                invoice = InvoiceEntity(
                    id = 0,
                    invoiceNumber = 0,
                    invoiceDate = 0L
                ),
                products = emptyList()
            )
        }

        val first = invoiceWithProducts.first()
        val invoice = InvoiceEntity(
            id = first.invoiceId,
            invoiceNumber = first.numberId,
            // Convert the string date back to a timestamp
            invoiceDate = try {
                first.invoiceDate.toLongOrNull() ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        )

        val productsWithQuantity = invoiceWithProducts.map { relation ->
            val product = relation.product
            ProductWithQuantity(
                product = com.example.composetrainer.domain.model.Product(
                    id = product.id,
                    name = product.name,
                    barcode = product.barcode,
                    price = product.price,
                    costPrice = product.costPrice,
                    description = product.description,
                    image = product.image,
                    subCategoryId = product.subcategoryId,
                    supplierId = product.supplierId,
                    unit = product.unit,
                    date = product.date,
                    stock = product.stock,
                    minStockLevel = product.minStockLevel,
                    maxStockLevel = product.maxStockLevel,
                    isActive = product.isActive,
                    tags = product.tags,
                    lastSoldDate = product.lastSoldDate
                ),
                quantity = relation.quantity
            )
        }

        return InvoiceWithProducts(invoice, productsWithQuantity)
    }
}
