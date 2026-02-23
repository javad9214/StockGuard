package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.InvoiceProduct


interface InvoiceProductRepository {

    suspend fun insertCrossRef(invoiceProduct: InvoiceProduct)


    suspend fun deleteCrossRef(invoiceProduct: InvoiceProduct)


    suspend fun getInvoiceWithProducts(invoiceId: Long): List<InvoiceProduct>


    suspend fun getAllInvoiceWithProducts(invoiceId: Long): List<InvoiceProduct>
}
