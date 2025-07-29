package com.example.composetrainer.data.repository

import com.example.composetrainer.data.local.dao.InvoiceDao
import com.example.composetrainer.data.local.dao.InvoiceProductDao
import com.example.composetrainer.data.local.dao.ProductDao
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.relation.InvoiceWithProductsRelation
import com.example.composetrainer.domain.model.InvoiceProduct
import com.example.composetrainer.domain.model.InvoiceProductFactory
import com.example.composetrainer.domain.model.InvoiceWithProducts
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.domain.model.TopSellingProductInfo
import com.example.composetrainer.domain.model.toDomain
import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.usecase.sales.AddToProductSalesSummaryUseCase
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

    }

    override suspend fun getInvoiceWithProducts(invoiceId: Long): Flow<InvoiceWithProducts>{
        return invoiceDao.getInvoiceWithProducts(invoiceId).map {
            mapToInvoiceWithProducts(it)
        }
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

    override suspend fun getTotalInvoicesBetweenDates(start: Long, end: Long): Int {
        return invoiceDao.getTotalInvoicesBetweenDates(start, end)
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
    private fun mapToInvoiceWithProducts(
        invoiceWithProductsRelations: List<InvoiceWithProductsRelation>
    ): InvoiceWithProducts {
        require(invoiceWithProductsRelations.isNotEmpty()) {
            "InvoiceWithProductsRelations list cannot be empty"
        }

        // All relations should have the same invoice
        val firstRelation = invoiceWithProductsRelations.first()
        val invoiceEntity = firstRelation.invoice

        // Validate that all relations belong to the same invoice
        require(invoiceWithProductsRelations.all { it.invoice.id == invoiceEntity.id }) {
            "All relations must belong to the same invoice"
        }

        // Map InvoiceEntity to Invoice domain model
        val invoice = invoiceEntity.toDomain()

        // Map ProductEntities to InvoiceProduct domain models
        val products = firstRelation.invoiceProducts.map { it.toDomain() }

        return InvoiceWithProducts(
            invoice = invoice,
            products = products
        )
    }

    // Alternative version if you need to handle empty lists
    private fun mapToInvoiceWithProductsOrNull(
        invoiceWithProductsRelations: List<InvoiceWithProductsRelation>
    ): InvoiceWithProducts? {
        if (invoiceWithProductsRelations.isEmpty()) return null
        return mapToInvoiceWithProducts(invoiceWithProductsRelations)
    }

    // Extension function version for cleaner usage
    private fun List<InvoiceWithProductsRelation>.toInvoiceWithProducts(): InvoiceWithProducts {
        return mapToInvoiceWithProducts(this)
    }

    // Group multiple invoices if needed
    private fun mapToMultipleInvoicesWithProducts(
        invoiceWithProductsRelations: List<InvoiceWithProductsRelation>
    ): List<InvoiceWithProducts> {
        return invoiceWithProductsRelations
            .groupBy { it.invoiceId }
            .map { (_, relations) ->
                mapToInvoiceWithProducts(relations)
            }
    }
}
