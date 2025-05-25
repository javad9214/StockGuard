package com.example.composetrainer.data.local.dao
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.composetrainer.data.local.relation.SubcategoryWithProducts

@Dao
interface SubcategoryDao {

    @Transaction
    @Query("SELECT * FROM subcategories WHERE categoryId = :categoryId")
    suspend fun getSubcategoriesWithProducts(categoryId: Int): List<SubcategoryWithProducts>
}
