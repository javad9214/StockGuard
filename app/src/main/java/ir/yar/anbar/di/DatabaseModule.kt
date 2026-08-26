package ir.yar.anbar.di

import android.content.Context
import androidx.room.Room
import ir.yar.anbar.data.local.database.AppDatabase
import ir.yar.anbar.data.local.database.MIGRATION_3_4
import ir.yar.anbar.data.local.database.MIGRATION_4_5
import ir.yar.anbar.data.local.database.MIGRATION_5_6
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context):AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "stock_guard_db"
        )
            .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            // v1/v2 predate server sync — their local-only rows can't be mapped.
            // Wiping is safe: the server copy is re-pulled on the next list load.
            .fallbackToDestructiveMigrationFrom(1, 2)
            .build()
    }


    @Provides
    @Singleton
    fun provideUserProductDao(appDatabase: AppDatabase) = appDatabase.userProductDao()

    @Provides
    @Singleton
    fun provideInvoiceDao(appDatabase: AppDatabase) = appDatabase.invoiceDao()

    @Provides
    @Singleton
    fun provideInvoiceProductDao(appDatabase: AppDatabase) = appDatabase.invoiceProductDao()

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase) = appDatabase.categoryDao()

    @Provides
    @Singleton
    fun provideSubCategoryDao(appDatabase: AppDatabase) = appDatabase.subCategoryDao()

    @Provides
    @Singleton
    fun provideProductSalesSummeryDao(appDatabase: AppDatabase) = appDatabase.productSalesSummaryDao()

    @Provides
    @Singleton
    fun provideCustomerDao(appDatabase: AppDatabase) = appDatabase.customerDao()

    @Provides
    @Singleton
    fun provideCustomerInvoiceSummaryDao(appDatabase: AppDatabase) = appDatabase.customerInvoiceSummaryDao()

    @Provides
    @Singleton
    fun provideStockMovementDao(appDatabase: AppDatabase) = appDatabase.stockMovementDao()

    @Provides
    @Singleton
    fun provideSupplierDao(appDatabase: AppDatabase) = appDatabase.supplierDao()

}