package com.example.composetrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.composetrainer.data.local.entity.InvoiceProductCrossRefEntity

@Dao
interface InvoiceProductDao {
    @Insert(onConflict = androidx.room.OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: InvoiceProductCrossRefEntity)

    @Delete
    suspend fun deleteCrossRef(crossRef: InvoiceProductCrossRefEntity)

    @Transaction
    @Query(
        """
        SELECT  ip.*
        FROM invoice_products AS ip 
        WHERE ip.invoiceId = :invoiceId
    """
    )
    suspend fun getInvoiceWithProducts(invoiceId: Long): List<InvoiceProductCrossRefEntity>


    @Transaction
    @Query(
        """
        SELECT  ip.*
        FROM invoice_products AS ip 
        WHERE ip.invoiceId = :invoiceId
    """
    )
    suspend fun getAllInvoiceWithProducts(invoiceId: Long): List<InvoiceProductCrossRefEntity>
}