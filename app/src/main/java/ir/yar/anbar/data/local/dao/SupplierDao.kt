package ir.yar.anbar.data.local.dao

import androidx.room.*
import ir.yar.anbar.data.local.entity.SupplierEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {
    @Insert
    suspend fun insert(supplier: SupplierEntity): Long

    @Update
    suspend fun update(supplier: SupplierEntity)

    @Delete
    suspend fun delete(supplier: SupplierEntity)

    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getById(id: Long): SupplierEntity?

    @Query("SELECT * FROM suppliers WHERE name LIKE '%' || :query || '%'")
    suspend fun searchByName(query: String): List<SupplierEntity>

    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllStream(): Flow<List<SupplierEntity>>
}