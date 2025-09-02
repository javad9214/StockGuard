package com.example.composetrainer.di

import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
import com.example.composetrainer.domain.usecase.analytics.GetAnalyticsDataUseCase
import com.example.composetrainer.domain.usecase.analytics.GetInvoiceReportCountUseCase
import com.example.composetrainer.domain.usecase.analytics.GetTotalProfitPriceUseCase
import com.example.composetrainer.domain.usecase.analytics.GetTotalSoldPriceUseCase
import com.example.composetrainer.domain.usecase.product.GetProductsByIDsUseCase
import com.example.composetrainer.domain.usecase.sales.GetProductSalesSummaryUseCase
import com.example.composetrainer.domain.usecase.sales.SaveProductSaleSummeryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyzeUseCaseModule {


    @Provides
    @Singleton
    fun provideGetAnalyticsDataUseCase(
        invoiceRepository: InvoiceRepository
    ): GetAnalyticsDataUseCase = GetAnalyticsDataUseCase(invoiceRepository)

    @Provides
    @Singleton
    fun provideGetInvoiceReportCountUseCase(
        invoiceRepository: InvoiceRepository
    ): GetInvoiceReportCountUseCase = GetInvoiceReportCountUseCase(invoiceRepository)


    @Provides
    @Singleton
    fun provideGetProductSalesSummaryUseCase(
        productSalesSummaryRepository: ProductSalesSummaryRepository,
        getProductsByIDsUseCase: GetProductsByIDsUseCase
    ): GetProductSalesSummaryUseCase =
        GetProductSalesSummaryUseCase(productSalesSummaryRepository, getProductsByIDsUseCase)

    @Provides
    @Singleton
    fun provideSaveProductSalesSummaryUseCase(
        productSalesSummaryRepository: ProductSalesSummaryRepository
    ): SaveProductSaleSummeryUseCase = SaveProductSaleSummeryUseCase(productSalesSummaryRepository)


    @Provides
    @Singleton
    fun provideGetTotalSoldPriceUseCase(
        invoiceRepository: InvoiceRepository
    ): GetTotalSoldPriceUseCase = GetTotalSoldPriceUseCase(invoiceRepository)


    @Provides
    @Singleton
    fun provideGetTotalProfitPriceUseCase(
        invoiceRepository: InvoiceRepository
    ): GetTotalProfitPriceUseCase = GetTotalProfitPriceUseCase(invoiceRepository)
}