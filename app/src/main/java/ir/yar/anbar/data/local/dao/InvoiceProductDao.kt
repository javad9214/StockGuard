package ir.yar.anbar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.yar.anbar.data.local.entity.InvoiceProductCrossRefEntity

@Dao
interface InvoiceProductDao {

    @Insert
    suspend fun insertCrossRef(crossRef: InvoiceProductCrossRefEntity)

    //region Server-sync

    @Query("SELECT * FROM invoice_products WHERE invoiceId = :invoiceId")
    suspend fun getCrossRefsForInvoice(invoiceId: Long): List<InvoiceProductCrossRefEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<InvoiceProductCrossRefEntity>)

    @Query("DELETE FROM invoice_products WHERE invoiceId = :invoiceId")
    suspend fun deleteCrossRefsForInvoice(invoiceId: Long)

    //endregion
}