package ir.yar.anbar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ir.yar.anbar.data.local.entity.InvoiceEntity
import ir.yar.anbar.data.local.relation.InvoiceWithProductsRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: Long)

    @Query("SELECT * FROM invoices ORDER BY invoiceNumber DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    // Get the last invoice (used to generate the next numberId)
    @Query("SELECT * FROM invoices ORDER BY invoiceNumber DESC LIMIT 1")
    suspend fun getLastInvoice(): InvoiceEntity?
    
    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun getInvoiceWithProducts(invoiceId: Long): Flow<InvoiceWithProductsRelation>

    @Transaction
    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAllInvoiceWithProducts(): Flow<List<InvoiceWithProductsRelation>>

    @Transaction
    @Query("SELECT * FROM invoices ORDER BY createdAt ASC")
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
    SELECT COALESCE(SUM((p.price - p.costPrice) * ip.quantity), 0) AS totalProfit
    FROM invoices AS i
    INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
    INNER JOIN products AS p ON ip.productId = p.id
    WHERE i.invoiceDate BETWEEN :startDate AND :endDate
    """
    )
    fun getTotalProfitBetweenDates(startDate: Long, endDate: Long): Flow<Long>

    @Query(
        """
    SELECT COALESCE(SUM(p.price * ip.quantity), 0) as totalSales
    FROM invoices AS i
    INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
    INNER JOIN products AS p ON ip.productId = p.id
    WHERE i.invoiceDate BETWEEN :startDate AND :endDate
    """
    )
    fun getTotalSalesBetweenDates(startDate: Long, endDate: Long): Flow<Long>

    @Query(
        """
    SELECT COUNT(*) FROM invoices
    WHERE invoiceDate BETWEEN :startDate AND :endDate
    """
    )
    fun getTotalInvoicesBetweenDates(startDate: Long, endDate: Long): Flow<Int>

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