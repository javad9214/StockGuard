package com.example.composetrainer.di

import com.example.composetrainer.domain.repository.InvoiceProductRepository
import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.repository.ProductRepository
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
import com.example.composetrainer.domain.repository.StockMovementRepository
import com.example.composetrainer.domain.usecase.product.AddProductUseCase
import com.example.composetrainer.domain.usecase.analytics.GetAnalyticsDataUseCase
import com.example.composetrainer.domain.usecase.invoice.GetAllInvoiceUseCase
import com.example.composetrainer.domain.usecase.invoice.GetInvoiceNumberUseCase
import com.example.composetrainer.domain.usecase.invoice.GetInvoiceWithDetailsUseCase
import com.example.composetrainer.domain.usecase.invoice.InitInvoiceWithProductsUseCase
import com.example.composetrainer.domain.usecase.invoice.InsertInvoiceUseCase
import com.example.composetrainer.domain.usecase.product.GetProductByQueryUseCase
import com.example.composetrainer.domain.usecase.sales.GetProductSalesSummaryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Invoice UseCases

    @Provides
    @Singleton
    fun provideGetInvoiceNumberUseCase(
        repository: InvoiceRepository
    ): GetInvoiceNumberUseCase = GetInvoiceNumberUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAllInvoicesUseCase(
        repository: InvoiceRepository
    ): GetAllInvoiceUseCase = GetAllInvoiceUseCase(repository)


    @Provides
    @Singleton
    fun provideGetInvoiceWithDetailsUseCase(
        repository: InvoiceRepository
    ): GetInvoiceWithDetailsUseCase = GetInvoiceWithDetailsUseCase(repository)

    @Provides
    @Singleton
    fun provideInitInvoiceWithProductUseCase(
        repository: InvoiceRepository,
        invoiceNumberUseCase: GetInvoiceNumberUseCase
    ): InitInvoiceWithProductsUseCase =
        InitInvoiceWithProductsUseCase(invoiceNumberUseCase, repository)

    @Provides
    @Singleton
    fun provideInsertInvoiceUseCase(
        invoiceRepository: InvoiceRepository,
        productSalesSummaryRepository: ProductSalesSummaryRepository,
        stockMovementRepository: StockMovementRepository,
        invoiceProductRepository: InvoiceProductRepository
    ): InsertInvoiceUseCase = InsertInvoiceUseCase(
        invoiceRepository,
        productSalesSummaryRepository,
        stockMovementRepository,
        invoiceProductRepository
    )

    // Product UseCases
    @Provides
    @Singleton
    fun provideAddProductUseCase(
        repository: ProductRepository
    ): AddProductUseCase = AddProductUseCase(repository)

    @Provides
    @Singleton
    fun provideGetProductsUseCase(
        repository: ProductRepository
    ): GetProductByQueryUseCase = GetProductByQueryUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAnalyticsDataUseCase(
        invoiceRepository: InvoiceRepository
    ): GetAnalyticsDataUseCase = GetAnalyticsDataUseCase(invoiceRepository)


    @Provides
    @Singleton
    fun provideGetProductSalesSummaryUseCase(
        productSalesSummaryRepository: ProductSalesSummaryRepository
    ): GetProductSalesSummaryUseCase =
        GetProductSalesSummaryUseCase(productSalesSummaryRepository)


}