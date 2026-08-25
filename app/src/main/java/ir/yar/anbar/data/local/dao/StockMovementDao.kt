package ir.yar.anbar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import ir.yar.anbar.data.local.entity.StockMovementEntity

@Dao
interface StockMovementDao {

    @Insert
    suspend fun insert(movement: StockMovementEntity): Long
}
