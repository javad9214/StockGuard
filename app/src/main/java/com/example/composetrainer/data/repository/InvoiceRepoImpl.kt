package com.example.composetrainer.data.repository

import com.example.composetrainer.data.local.dao.InvoiceDao
import com.example.composetrainer.data.local.dao.InvoiceProductDao
import com.example.composetrainer.data.local.dao.ProductDao
import com.example.composetrainer.data.relation.ProductWithQuantity
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InvoiceRepoImpl @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val invoiceProductDao: InvoiceProductDao,
    private val productDao: ProductDao
) : InvoiceRepository{

    override suspend fun createInvoice(products: List<ProductWithQuantity>): Invoice {
        TODO("Not yet implemented")
    }

    override fun getAllInvoices(): Flow<List<Invoice>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteInvoice(invoiceId: Long) {
        TODO("Not yet implemented")
    }

    private suspend fun getNextInvoiceNumberId(): Long {
        // Get the highest existing numberId and increment
        val lastInvoice = invoiceDao.getLastInvoice()
        return if (lastInvoice == null) 10000L else lastInvoice.numberId + 1
    }
}