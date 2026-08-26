package ir.yar.anbar.data.repository

import ir.yar.anbar.data.local.dao.StockMovementDao
import ir.yar.anbar.data.mapper.toEntity
import ir.yar.anbar.domain.model.StockMovement
import ir.yar.anbar.domain.repository.StockMovementRepository
import javax.inject.Inject

class StockMovementRepoImpl @Inject constructor(
    private val stockMovementDao: StockMovementDao
) : StockMovementRepository {

    override suspend fun insert(movement: StockMovement): Long {
        return stockMovementDao.insert(movement.toEntity())
    }
}
