package ir.yar.anbar.data.repository

import ir.yar.anbar.data.local.dao.StockMovementDao
import ir.yar.anbar.domain.model.StockMovement
import ir.yar.anbar.domain.model.toDomain
import ir.yar.anbar.domain.model.toEntity
import ir.yar.anbar.domain.repository.StockMovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StockMovementRepoImpl  @Inject constructor(
    private val stockMovementDao: StockMovementDao
) : StockMovementRepository {

    override suspend fun insert(movement: StockMovement): Long {
       return stockMovementDao.insert(movement.toEntity())
    }

    override suspend fun update(movement: StockMovement) {
        stockMovementDao.update(movement.toEntity())
    }

    override suspend fun delete(movement: StockMovement) {
        stockMovementDao.delete(movement.toEntity())
    }

    override fun getByProductId(productId: Long): Flow<List<StockMovement>> {
        return stockMovementDao.getByProductId(productId).map { it -> it.map { it.toDomain() }}
    }

    override suspend fun getByInvoiceId(invoiceId: Long): List<StockMovement> {
        return stockMovementDao.getByInvoiceId(invoiceId).map { it.toDomain() }
    }
}