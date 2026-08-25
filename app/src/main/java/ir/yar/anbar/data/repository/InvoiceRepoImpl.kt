package ir.yar.anbar.data.repository

import ir.yar.anbar.data.local.dao.InvoiceDao
import ir.yar.anbar.data.local.relation.InvoiceWithProductsRelation
import ir.yar.anbar.domain.model.Invoice
import ir.yar.anbar.domain.model.InvoiceWithProducts
import ir.yar.anbar.domain.model.TopSellingProductInfo
import ir.yar.anbar.domain.model.toDomain
import ir.yar.anbar.domain.model.toEntity
import ir.yar.anbar.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvoiceRepoImpl @Inject constructor(
    private val invoiceDao: InvoiceDao
) : InvoiceRepository {

    override suspend fun createInvoice(invoice: Invoice): Long {
        return invoiceDao.insertInvoice(invoice.toEntity())
    }

    override fun getInvoiceWithProducts(invoiceId: Long): Flow<InvoiceWithProducts> {
        return invoiceDao.getInvoiceWithProducts(invoiceId).map { relation ->
            mapToInvoiceWithProducts(relation)
        }
    }

    override fun getAllInvoices(): Flow<List<InvoiceWithProducts>> {
        return invoiceDao.getAllInvoiceWithProducts().map { list ->
            list.map { mapToInvoiceWithProducts(it) }
        }
    }

    override fun getAllInvoicesOldestFirst(): Flow<List<InvoiceWithProducts>> {
        return invoiceDao.getAllInvoiceWithProductsOldestFirst().map { list ->
            list.map { mapToInvoiceWithProducts(it) }
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

    override fun getTotalProfitBetweenDates(start: Long, end: Long): Flow<Long> {
        return invoiceDao.getTotalProfitBetweenDates(start, end)
    }

    override fun getTotalSalesBetweenDates(start: Long, end: Long): Flow<Long> {
        return invoiceDao.getTotalSalesBetweenDates(start, end)
    }

    override fun getTotalInvoicesBetweenDates(start: Long, end: Long): Flow<Int> {
        return invoiceDao.getTotalInvoicesBetweenDates(start, end)
    }

    private fun mapToInvoiceWithProducts(
        invoiceWithProductsRelation: InvoiceWithProductsRelation
    ): InvoiceWithProducts {
        val invoice = invoiceWithProductsRelation.invoice.toDomain()
        val products = invoiceWithProductsRelation.invoiceProducts.map { it.product.toDomain() }
        val invoiceProducts = invoiceWithProductsRelation.invoiceProducts.map {
            it.invoiceProductsCrossRef.toDomain()
        }

        return InvoiceWithProducts(
            invoice = invoice,
            invoiceProducts = invoiceProducts,
            products = products
        )
    }
}
