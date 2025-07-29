package com.example.composetrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.relation.InvoiceWithProductsRelation
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
    @Query(
        """
        SELECT i.id AS invoiceId,i.*, ip.*
        FROM invoices AS i
        JOIN invoice_products AS ip ON i.id = ip.invoiceId
        WHERE i.id = :invoiceId
    """
    )
    fun getInvoiceWithProducts(invoiceId: Long): Flow<List<InvoiceWithProductsRelation>>

    @Transaction
    @Query(
        """
        SELECT i.id AS invoiceId,i.*, ip.quantity
        FROM invoices AS i 
        INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
        ORDER BY i.createdAt DESC
    """
    )
    fun getAllInvoiceWithProducts(): Flow<List<InvoiceWithProductsRelation>>

    @Transaction
    @Query(
        """
        SELECT i.id AS invoiceId,i.*, ip.quantity
        FROM invoices AS i 
        INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
        ORDER BY i.createdAt ASC
    """
    )
    fun getAllInvoiceWithProductsOldestFirst(): Flow<List<InvoiceWithProductsRelation>>

    // Analytics queries
    @Query(
        """
        SELECT COALESCE(SUM(p.price * ip.quantity), 0) as totalSales
        FROM invoices AS i
        INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
        INNER JOIN products AS p ON ip.productId = p.id
        WHERE strftime('%Y-%m', datetime(i.invoiceDate / 1000, 'unixepoch')) = :yearMonth
    """
    )
    suspend fun getTotalSalesForMonth(yearMonth: String): Long

    @Query(
        """
        SELECT COUNT(DISTINCT i.id) as invoiceCount
        FROM invoices AS i
        WHERE strftime('%Y-%m', datetime(i.invoiceDate / 1000, 'unixepoch')) = :yearMonth
    """
    )
    suspend fun getTotalInvoicesForMonth(yearMonth: String): Int

    @Query(
        """
        SELECT COALESCE(SUM(ip.quantity), 0) as totalQuantity
        FROM invoices AS i
        INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
        WHERE strftime('%Y-%m', datetime(i.invoiceDate / 1000, 'unixepoch')) = :yearMonth
    """
    )
    suspend fun getTotalQuantityForMonth(yearMonth: String): Int

    @Query(
        """
        SELECT p.name, 
               SUM(ip.quantity) as totalQuantity,
               SUM(p.price * ip.quantity) as totalSales
        FROM invoices AS i
        INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
        INNER JOIN products AS p ON ip.productId = p.id
        WHERE strftime('%Y-%m', datetime(i.invoiceDate / 1000, 'unixepoch')) = :yearMonth
        GROUP BY p.id, p.name
        ORDER BY totalQuantity DESC
        LIMIT 3
    """
    )
    suspend fun getTopSellingProductsForMonth(yearMonth: String): List<TopSellingProduct>

    @Query(
        """
    SELECT COUNT(*) FROM invoices
    WHERE invoiceDate BETWEEN :startDate AND :endDate
    """
    )
    suspend fun getTotalInvoicesBetweenDates(startDate: Long, endDate: Long): Int

    // Debug queries
    @Query("SELECT * FROM invoices ORDER BY createdAt DESC LIMIT 5")
    suspend fun getRecentInvoicesForDebug(): List<InvoiceEntity>

    @Query("SELECT COUNT(*) FROM invoices")
    suspend fun getTotalInvoiceCount(): Int
}

data class TopSellingProduct(
    val name: String,
    val totalQuantity: Int,
    val totalSales: Long
)