package ir.yar.anbar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import ir.yar.anbar.data.local.entity.InvoiceProductCrossRefEntity

@Dao
interface InvoiceProductDao {

    @Insert
    suspend fun insertCrossRef(crossRef: InvoiceProductCrossRefEntity)
}
