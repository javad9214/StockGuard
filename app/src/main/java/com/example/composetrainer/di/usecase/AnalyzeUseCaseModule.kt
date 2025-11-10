package com.example.composetrainer.di.usecase

import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.repository.ProductRepository
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
import com.example.composetrainer.domain.usecase.analytics.GetAnalyticsDataUseCase
import com.example.composetrainer.domain.usecase.analytics.GetInvoiceReportCountUseCase
import com.example.composetrainer.domain.usecase.analytics.GetLowStockProductsUseCase
import com.example.composetrainer.domain.usecase.analytics.GetTotalProfitPriceUseCase
import com.example.composetrainer.domain.usecase.analytics.GetTotalSoldPriceUseCase
import com.example.composetrainer.domain.usecase.product.GetProductsByIDsUseCase
import com.example.composetrainer.domain.usecase.sales.GetTopProfitableProductsUseCase
import com.example.composetrainer.domain.usecase.sales.GetTopSellingProductsUseCase
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
    fun provideGetTopSellingProductSalesSummaryUseCase(
        productSalesSummaryRepository: ProductSalesSummaryRepository,
        getProductsByIDsUseCase: GetProductsByIDsUseCase
    ): GetTopSellingProductsUseCase =
        GetTopSellingProductsUseCase(productSalesSummaryRepository, getProductsByIDsUseCase)



    @Provides
    @Singleton
    fun provideGetTopProfitableProductSalesSummaryUseCase(
        productSalesSummaryRepository: ProductSalesSummaryRepository,
        getProductsByIDsUseCase: GetProductsByIDsUseCase
    ): GetTopProfitableProductsUseCase =
        GetTopProfitableProductsUseCase(productSalesSummaryRepository, getProductsByIDsUseCase)

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

    @Provides
    @Singleton
    fun provideGetLowStockProductsUseCase(
        productRepository: ProductRepository
    ): GetLowStockProductsUseCase = GetLowStockProductsUseCase(productRepository)
}