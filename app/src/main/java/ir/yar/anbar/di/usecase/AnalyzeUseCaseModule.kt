package ir.yar.anbar.di.usecase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.yar.anbar.domain.repository.InvoiceRepository
import ir.yar.anbar.domain.repository.ProductRepository
import ir.yar.anbar.domain.repository.ProductSalesSummaryRepository
import ir.yar.anbar.domain.usecase.analytics.GetAnalyticsDataUseCase
import ir.yar.anbar.domain.usecase.analytics.GetInvoiceReportCountUseCase
import ir.yar.anbar.domain.usecase.analytics.GetLowStockProductsUseCase
import ir.yar.anbar.domain.usecase.analytics.GetTotalProfitPriceUseCase
import ir.yar.anbar.domain.usecase.analytics.GetTotalSoldPriceUseCase
import ir.yar.anbar.domain.usecase.product.GetProductsByIDsUseCase
import ir.yar.anbar.domain.usecase.sales.GetTopProfitableProductsUseCase
import ir.yar.anbar.domain.usecase.sales.GetTopSellingProductsUseCase
import ir.yar.anbar.domain.usecase.sales.SaveProductSaleSummeryUseCase
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