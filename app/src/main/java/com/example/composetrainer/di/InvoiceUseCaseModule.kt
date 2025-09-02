package com.example.composetrainer.di

import com.example.composetrainer.domain.repository.InvoiceProductRepository
import com.example.composetrainer.domain.repository.InvoiceRepository
import com.example.composetrainer.domain.repository.ProductSalesSummaryRepository
import com.example.composetrainer.domain.repository.StockMovementRepository
import com.example.composetrainer.domain.usecase.invoice.DeleteInvoiceUseCase
import com.example.composetrainer.domain.usecase.invoice.GetAllInvoiceUseCase
import com.example.composetrainer.domain.usecase.invoice.GetInvoiceNumberUseCase
import com.example.composetrainer.domain.usecase.invoice.GetInvoiceWithDetailsUseCase
import com.example.composetrainer.domain.usecase.invoice.InitInvoiceWithProductsUseCase
import com.example.composetrainer.domain.usecase.invoice.InsertInvoiceUseCase
import com.example.composetrainer.domain.usecase.sales.SaveProductSaleSummeryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InvoiceUseCaseModule {


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
        invoiceNumberUseCase: GetInvoiceNumberUseCase
    ): InitInvoiceWithProductsUseCase =
        InitInvoiceWithProductsUseCase(invoiceNumberUseCase)

    @Provides
    @Singleton
    fun provideInsertInvoiceUseCase(
        invoiceRepository: InvoiceRepository,
        stockMovementRepository: StockMovementRepository,
        invoiceProductRepository: InvoiceProductRepository,
        saveProductSaleSummeryUseCase: SaveProductSaleSummeryUseCase
    ): InsertInvoiceUseCase = InsertInvoiceUseCase(
        invoiceRepository,
        stockMovementRepository,
        invoiceProductRepository,
        saveProductSaleSummeryUseCase
    )

    @Provides
    @Singleton
    fun provideDeleteInvoiceUseCase(
        invoiceRepository: InvoiceRepository
    ): DeleteInvoiceUseCase = DeleteInvoiceUseCase(invoiceRepository)





}