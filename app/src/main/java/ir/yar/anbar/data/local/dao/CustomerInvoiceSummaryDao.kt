package ir.yar.anbar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.yar.anbar.data.local.entity.CustomerInvoiceSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerInvoiceSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(summary: CustomerInvoiceSummaryEntity)
    
    @Query("SELECT * FROM customer_invoice_summary WHERE customerId = :customerId")
    suspend fun getByCustomerId(customerId: Long): CustomerInvoiceSummaryEntity?
    
    @Query("SELECT * FROM customer_invoice_summary ORDER BY totalDebt DESC")
    fun getAllStream(): Flow<List<CustomerInvoiceSummaryEntity>>
}