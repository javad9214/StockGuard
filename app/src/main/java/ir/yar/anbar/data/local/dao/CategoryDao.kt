package ir.yar.anbar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.yar.anbar.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {

    // REPLACE so a server refresh can update names/icons of cached rows;
    // IGNORE would keep stale data forever
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)
}
