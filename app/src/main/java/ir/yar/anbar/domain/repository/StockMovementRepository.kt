package ir.yar.anbar.domain.repository

import ir.yar.anbar.domain.model.StockMovement
import kotlinx.coroutines.flow.Flow


interface StockMovementRepository {

    suspend fun insert(movement: StockMovement): Long

    suspend fun update(movement: StockMovement)

    suspend fun delete(movement: StockMovement)


    fun getByProductId(productId: Long): Flow<List<StockMovement>>

    suspend fun getByInvoiceId(invoiceId: Long): List<StockMovement>
}
