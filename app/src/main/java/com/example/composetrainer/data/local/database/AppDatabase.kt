package com.example.composetrainer.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composetrainer.data.local.dao.CategoryDao
import com.example.composetrainer.data.local.dao.CustomerDao
import com.example.composetrainer.data.local.dao.CustomerInvoiceSummaryDao
import com.example.composetrainer.data.local.dao.InvoiceDao
import com.example.composetrainer.data.local.dao.InvoiceProductDao
import com.example.composetrainer.data.local.dao.ProductDao
import com.example.composetrainer.data.local.dao.ProductSalesSummaryDao
import com.example.composetrainer.data.local.dao.StockMovementDao
import com.example.composetrainer.data.local.dao.SubcategoryDao
import com.example.composetrainer.data.local.dao.SupplierDao
import com.example.composetrainer.data.local.entity.CategoryEntity
import com.example.composetrainer.data.local.entity.CustomerEntity
import com.example.composetrainer.data.local.entity.CustomerInvoiceSummaryEntity
import com.example.composetrainer.data.local.entity.InvoiceEntity
import com.example.composetrainer.data.local.entity.InvoiceProductCrossRefEntity
import com.example.composetrainer.data.local.entity.ProductEntity
import com.example.composetrainer.data.local.entity.ProductSalesSummaryEntity
import com.example.composetrainer.data.local.entity.StockMovementEntity
import com.example.composetrainer.data.local.entity.SubcategoryEntity
import com.example.composetrainer.data.local.entity.SupplierEntity


@Database(
    entities = [
        ProductEntity::class,
        InvoiceEntity::class,
        InvoiceProductCrossRefEntity::class,
        CategoryEntity::class,
        SubcategoryEntity::class,
        ProductSalesSummaryEntity::class,
        CustomerEntity::class,
        CustomerInvoiceSummaryEntity::class,
        StockMovementEntity::class,
        SupplierEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceProductDao(): InvoiceProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subCategoryDao(): SubcategoryDao
    abstract fun productSalesSummaryDao(): ProductSalesSummaryDao
    abstract fun customerDao(): CustomerDao
    abstract fun customerInvoiceSummaryDao(): CustomerInvoiceSummaryDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun supplierDao(): SupplierDao
}