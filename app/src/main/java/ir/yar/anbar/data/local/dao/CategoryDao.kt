package ir.yar.anbar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ir.yar.anbar.data.local.entity.CategoryEntity
import ir.yar.anbar.data.local.relation.CategoryWithSubcategories

@Dao
interface CategoryDao {

    @Transaction
    @Query("SELECT * FROM categories")
    suspend fun getCategoriesWithSubcategories(): List<CategoryWithSubcategories>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>): List<Long>

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): CategoryEntity?
}
