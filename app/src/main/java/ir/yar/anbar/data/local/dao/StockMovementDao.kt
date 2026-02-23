package ir.yar.anbar.data.local.dao

import androidx.room.*
import ir.yar.anbar.data.local.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMovementDao {
    @Insert
    suspend fun insert(movement: StockMovementEntity): Long

    @Update
    suspend fun update(movement: StockMovementEntity)

    @Delete
    suspend fun delete(movement: StockMovementEntity)

    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY createdAt DESC")
    fun getByProductId(productId: Long): Flow<List<StockMovementEntity>>

    @Query("SELECT * FROM stock_movements WHERE sourceInvoiceId = :invoiceId")
    suspend fun getByInvoiceId(invoiceId: Long): List<StockMovementEntity>
}