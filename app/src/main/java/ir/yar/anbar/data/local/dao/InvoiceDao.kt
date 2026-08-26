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

    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getInvoiceById(invoiceId: Long): InvoiceEntity?

    //region Server-sync

    @Query("SELECT * FROM invoices WHERE serverId IN (:serverIds)")
    suspend fun getInvoicesByServerIds(serverIds: List<Long>): List<InvoiceEntity>

    @Query("SELECT * FROM invoices WHERE synced = 0")
    suspend fun getUnsyncedInvoices(): List<InvoiceEntity>

    @Query("UPDATE invoices SET synced = 1, serverId = :serverId, updatedAt = :now WHERE id = :id")
    suspend fun markInvoiceSynced(id: Long, serverId: Long, now: Long)

    /**
     * Tombstone for a synced invoice: the row (hidden by the isDeleted filters
     * below) survives until the push-sync uploads the deletion, then
     * [deleteInvoice] removes it for good.
     */
    @Query("UPDATE invoices SET isDeleted = 1, synced = 0, updatedAt = :now WHERE id = :id")
    suspend fun markInvoiceDeletedForSync(id: Long, now: Long)

    //endregion

    @Query("SELECT * FROM invoices ORDER BY invoiceNumber DESC LIMIT 1")
    suspend fun getLastInvoice(): InvoiceEntity?

    // Tombstones are filtered out so a delete pending its push disappears
    // from the UI immediately
    @Transaction
    @Query("SELECT * FROM invoices WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllInvoiceWithProducts(): Flow<List<InvoiceWithProductsRelation>>

    @Transaction
    @Query("SELECT * FROM invoices WHERE isDeleted = 0 ORDER BY createdAt ASC")
    fun getAllInvoiceWithProductsOldestFirst(): Flow<List<InvoiceWithProductsRelation>>

    @Query(
        """
    SELECT COALESCE(SUM((p.price - p.costPrice) * ip.quantity), 0) AS totalProfit
    FROM invoices AS i
    INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
    INNER JOIN user_products AS p ON ip.productId = p.id
    WHERE i.invoiceDate BETWEEN :startDate AND :endDate
    AND i.isDeleted = 0
    """
    )
    fun getTotalProfitBetweenDates(startDate: Long, endDate: Long): Flow<Long>

    @Query(
        """
    SELECT COALESCE(SUM(p.price * ip.quantity), 0) as totalSales
    FROM invoices AS i
    INNER JOIN invoice_products AS ip ON i.id = ip.invoiceId
    INNER JOIN user_products AS p ON ip.productId = p.id
    WHERE i.invoiceDate BETWEEN :startDate AND :endDate
    AND i.isDeleted = 0
    """
    )
    fun getTotalSalesBetweenDates(startDate: Long, endDate: Long): Flow<Long>

    @Query(
        """
    SELECT COUNT(*) FROM invoices
    WHERE invoiceDate BETWEEN :startDate AND :endDate
    AND isDeleted = 0
    """
    )
    fun getTotalInvoicesBetweenDates(startDate: Long, endDate: Long): Flow<Int>
}