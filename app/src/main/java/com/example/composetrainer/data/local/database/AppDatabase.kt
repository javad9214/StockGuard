package com.example.composetrainer.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composetrainer.data.local.dao.CategoryDao
import com.example.composetrainer.data.local.dao.InvoiceDao
import com.example.composetrainer.data.local.dao.InvoiceProductDao
import com.example.composetrainer.data.local.dao.ProductDao
import com.example.composetrainer.data.local.dao.SubcategoryDao
import com.example.composetrainer.data.local.entity.CategoryEntity
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.entity.InvoiceProductCrossRef
import com.example.composetrainer.data.local.entity.ProductEntity
import com.example.composetrainer.data.local.entity.SubcategoryEntity


@Database(
    entities = [ProductEntity::class, InvoiceEntity::class, InvoiceProductCrossRef::class, CategoryEntity::class, SubcategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceProductDao(): InvoiceProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subCategoryDao(): SubcategoryDao
}