package ir.yar.anbar.data.repository

import ir.yar.anbar.data.local.dao.InvoiceProductDao
import ir.yar.anbar.data.mapper.toEntity
import ir.yar.anbar.domain.model.InvoiceProduct
import ir.yar.anbar.domain.repository.InvoiceProductRepository
import javax.inject.Inject

class InvoiceProductRepoImpl @Inject constructor(
    private val invoiceProductDao: InvoiceProductDao
) : InvoiceProductRepository {

    override suspend fun insertCrossRef(invoiceProduct: InvoiceProduct) {
        invoiceProductDao.insertCrossRef(invoiceProduct.toEntity())
    }
}
