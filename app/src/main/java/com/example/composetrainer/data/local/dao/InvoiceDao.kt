package com.example.composetrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.relation.InvoiceWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {

    @Insert
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: Long)

    @Query("SELECT * FROM invoices ORDER BY invoiceNumber DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    // Get the last invoice (used to generate the next numberId)
    @Query("SELECT * FROM invoices ORDER BY invoiceNumber DESC LIMIT 1")
    suspend fun getLastInvoice(): InvoiceEntity?

    @Transaction
    @Query("""
        SELECT i.id AS invoiceId, i.invoiceNumber as numberId, i.invoiceDate AS invoiceDate, p.*, ip.quantity
        FROM invoices AS i
        JOIN invoice_products AS ip ON i.id = ip.invoiceId
        JOIN products AS p ON p.id = ip.productId
        WHERE i.id = :invoiceId
    """)
    suspend fun getInvoiceWithProducts(invoiceId: Long): List<InvoiceWithProduct>

    @Transaction
    @Query("""
        SELECT i.id AS invoiceId, i.invoiceNumber as numberId, i.invoiceDate AS invoiceDate, p.*, ip.quantity
        FROM invoices AS i 
        INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
        INNER JOIN products AS p ON ip.productId = p.id
    """)
    fun getAllInvoiceWithProducts(): Flow<List<InvoiceWithProduct>>




}