package com.example.composetrainer.di

import com.example.composetrainer.data.remote.api.ApiServiceMainProduct
import com.example.composetrainer.data.repository.InvoiceProductRepoImpl
import com.example.composetrainer.data.repository.InvoiceRepoImpl
import com.example.composetrainer.data.repository.ProductRepoImpl
import com.example.composetrainer.data.repository.ProductSalesSummaryRepoImpl
import com.example.composetrainer.data.repository.ServerMainProductRepoImpl
import com.example.composetrainer.data.repository.StockMovementRepoImpl
import com.example.composetrainer.domain.repository.InvoiceProductRepository
import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.repository.ProductRepository
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
import com.example.composetrainer.domain.repository.ServerMainProductRepository
import com.example.composetrainer.domain.repository.StockMovementRepository
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
}