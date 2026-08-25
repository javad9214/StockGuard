package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.StockMovement

interface StockMovementRepository {

    suspend fun insert(movement: StockMovement): Long
}
