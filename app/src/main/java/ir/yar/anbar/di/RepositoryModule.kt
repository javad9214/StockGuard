package ir.yar.anbar.di

import ir.yar.anbar.data.remote.api.ApiServiceMainProduct
import ir.yar.anbar.data.repository.InvoiceProductRepoImpl
import ir.yar.anbar.data.repository.InvoiceRepoImpl
import ir.yar.anbar.data.repository.ProductRepoImpl
import ir.yar.anbar.data.repository.ProductSalesSummaryRepoImpl
import ir.yar.anbar.data.repository.ServerMainProductRepoImpl
import ir.yar.anbar.data.repository.StockMovementRepoImpl
import ir.yar.anbar.data.repository.UserPreferencesRepositoryImpl
import ir.yar.anbar.domain.repository.InvoiceProductRepository
import ir.yar.anbar.domain.repository.InvoiceRepository
import ir.yar.anbar.domain.repository.ProductRepository
import ir.yar.anbar.domain.repository.ProductSalesSummaryRepository
import ir.yar.anbar.domain.repository.ServerMainProductRepository
import ir.yar.anbar.domain.repository.StockMovementRepository
import ir.yar.anbar.domain.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideInvoiceProductRepository(
        repoImpl: InvoiceProductRepoImpl
    ): InvoiceProductRepository = repoImpl

    @Provides
    @Singleton
    fun provideProductRepository(
        repoImpl: ProductRepoImpl
    ): ProductRepository = repoImpl

    @Provides
    @Singleton
    fun provideInvoiceRepository(
        repoImpl: InvoiceRepoImpl
    ): InvoiceRepository = repoImpl

    @Provides
    @Singleton
    fun provideProductSalesSummaryRepository(
        repoImpl: ProductSalesSummaryRepoImpl
    ): ProductSalesSummaryRepository = repoImpl

    @Provides
    @Singleton
    fun provideStockMovementRepository(
        repoImpl: StockMovementRepoImpl
    ): StockMovementRepository = repoImpl

    @Provides
    @Singleton
    fun provideServerMainProductRepository(
        apiServiceMainProduct: ApiServiceMainProduct
    ): ServerMainProductRepository {
        return ServerMainProductRepoImpl(apiServiceMainProduct = apiServiceMainProduct)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        repoImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository = repoImpl
}