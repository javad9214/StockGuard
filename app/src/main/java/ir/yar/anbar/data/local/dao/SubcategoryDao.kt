package ir.yar.anbar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.yar.anbar.data.local.entity.SubcategoryEntity

@Dao
interface SubcategoryDao {

    // REPLACE so a server refresh can update names/icons of cached rows;
    // IGNORE would keep stale data forever
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subcategories: List<SubcategoryEntity>)

    @Query("SELECT * FROM subcategories WHERE isDeleted = 0 ORDER BY name")
    suspend fun getAll(): List<SubcategoryEntity>
}
