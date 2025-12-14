package ir.yar.anbar.data.local.dao

import androidx.room.*
import ir.yar.anbar.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Insert
    suspend fun insert(customer: CustomerEntity): Long

    @Update
    suspend fun update(customer: CustomerEntity)

    @Delete
    suspend fun delete(customer: CustomerEntity)

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getById(id: Long): CustomerEntity?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%'")
    suspend fun searchByName(query: String): List<CustomerEntity>

    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllStream(): Flow<List<CustomerEntity>>
}