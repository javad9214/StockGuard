package ir.yar.anbar.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ir.yar.anbar.data.local.entity.SubcategoryEntity
import ir.yar.anbar.data.local.relation.SubcategoryWithProducts

@Dao
interface SubcategoryDao {

    @Transaction
    @Query("SELECT * FROM subcategories WHERE categoryId = :categoryId")
    suspend fun getSubcategoriesWithProducts(categoryId: Int): List<SubcategoryWithProducts>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(subcategory: SubcategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(subcategories: List<SubcategoryEntity>): List<Long>

    @Query("SELECT * FROM subcategories WHERE name = :name AND categoryId = :categoryId LIMIT 1")
    suspend fun getByNameAndCategoryId(name: String, categoryId: Int): SubcategoryEntity?
}
