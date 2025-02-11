package com.example.composetrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.relation.InvoiceWithProducts
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {

    @Insert
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: Long)

    @Query("SELECT * FROM invoices")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    // Get the last invoice (used to generate the next numberId)
    @Query("SELECT * FROM invoices ORDER BY numberId DESC LIMIT 1")
    suspend fun getLastInvoice(): InvoiceEntity?

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getInvoiceWithProducts(invoiceId: Long): InvoiceWithProducts
}