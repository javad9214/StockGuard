package com.example.composetrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.composetrainer.data.local.relation.CategoryWithSubcategories

@Dao
interface CategoryDao {

    @Transaction
    @Query("SELECT * FROM categories")
    suspend fun getCategoriesWithSubcategories(): List<CategoryWithSubcategories>
}
