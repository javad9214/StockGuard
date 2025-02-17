package com.example.composetrainer.data.repository

import com.example.composetrainer.data.local.dao.InvoiceDao
import com.example.composetrainer.data.local.dao.InvoiceProductDao
import com.example.composetrainer.data.local.dao.ProductDao
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.entity.InvoiceProductCrossRef
import com.example.composetrainer.data.local.relation.InvoiceWithProducts
import com.example.composetrainer.data.local.relation.EntityProductWithQuantity
import com.example.composetrainer.data.mapper.ProductMapper
import com.example.composetrainer.domain.model.Invoice
import com.example.composetrainer.domain.model.ProductWithQuantity
import com.example.composetrainer.domain.repository.InvoiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InvoiceRepoImpl @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val invoiceProductDao: InvoiceProductDao,
    private val productDao: ProductDao
) : InvoiceRepository {

    override suspend fun createInvoice(products: List<ProductWithQuantity>): Invoice {
        val nextNumberId = getNextInvoiceNumberId()

        val invoiceEntity = InvoiceEntity(
            numberId = nextNumberId,
            dateTime = System.currentTimeMillis()
        )

        // Insert invoice and get its generated ID
        val invoiceId = invoiceDao.insertInvoice(invoiceEntity)

        // Insert Product into invoice
        products.forEach { productWithQuantity ->
            invoiceProductDao.insertCrossRef(
                InvoiceProductCrossRef(
                    invoiceId = invoiceId,
                    productId = productWithQuantity.product.id,
                    quantity = productWithQuantity.quantity
                )
            )

            // Update product stock
            productDao.updateProduct(
                ProductMapper.toEntity(
                    productWithQuantity.product.copy(
                        stock = productWithQuantity.product.stock - productWithQuantity.quantity
                    )
                )

            )

        }

        return invoiceDao.getInvoiceWithProducts(invoiceId).toDomain()
    }

    override fun getAllInvoices(): Flow<List<Invoice>> {
        return invoiceDao.getAllInvoices().map { invoiceEntities ->
            invoiceEntities.map { invoiceEntity ->
                val invoiceWithProducts = invoiceDao.getInvoiceWithProducts(invoiceEntity.id)
                invoiceWithProducts.toDomain()
            }
        }
    }

    override suspend fun deleteInvoice(invoiceId: Long) {
        withContext(Dispatchers.IO) {
            val invoiceWithProducts = invoiceDao.getInvoiceWithProducts(invoiceId)
            invoiceWithProducts.products.forEach { productWithQuantity ->
                val product = productWithQuantity.product
                productDao.updateProduct(
                    product.copy(
                        stock = product.stock + productWithQuantity.quantity
                    )
                )
            }

            invoiceDao.deleteInvoice(invoiceId)
        }
    }

    private suspend fun getNextInvoiceNumberId(): Long {
        // Get the highest existing numberId and increment
        val lastInvoice = invoiceDao.getLastInvoice()
        return if (lastInvoice == null) 10000L else lastInvoice.numberId + 1
    }

    private fun InvoiceWithProducts.toDomain(): Invoice {
        val totalPrice = products.sumOf { it.product.price?.times(it.quantity) ?: 0L }
        return Invoice(
            id = invoice.id,
            numberId = invoice.numberId,
            dateTime = invoice.dateTime,
            products = products.map { it.toDomain() },
            totalPrice = totalPrice
        )
    }
}