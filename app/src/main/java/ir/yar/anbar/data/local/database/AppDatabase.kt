package ir.yar.anbar.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.yar.anbar.data.local.dao.CategoryDao
import ir.yar.anbar.data.local.dao.CustomerDao
import ir.yar.anbar.data.local.dao.CustomerInvoiceSummaryDao
import ir.yar.anbar.data.local.dao.InvoiceDao
import ir.yar.anbar.data.local.dao.InvoiceProductDao
import ir.yar.anbar.data.local.dao.UserProductDao
import ir.yar.anbar.data.local.dao.ProductSalesSummaryDao
import ir.yar.anbar.data.local.dao.StockMovementDao
import ir.yar.anbar.data.local.dao.SubcategoryDao
import ir.yar.anbar.data.local.dao.SupplierDao
import ir.yar.anbar.data.local.entity.CategoryEntity
import ir.yar.anbar.data.local.entity.CustomerEntity
import ir.yar.anbar.data.local.entity.CustomerInvoiceSummaryEntity
import ir.yar.anbar.data.local.entity.InvoiceEntity
import ir.yar.anbar.data.local.entity.InvoiceProductCrossRefEntity
import ir.yar.anbar.data.local.entity.UserProductEntity
import ir.yar.anbar.data.local.entity.ProductSalesSummaryEntity
import ir.yar.anbar.data.local.entity.StockMovementEntity
import ir.yar.anbar.data.local.entity.SubcategoryEntity
import ir.yar.anbar.data.local.entity.SupplierEntity


@Database(
    entities = [
        UserProductEntity::class,
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
    abstract fun productDao(): UserProductDao
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