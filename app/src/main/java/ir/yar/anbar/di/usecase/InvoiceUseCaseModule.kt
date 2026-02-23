package ir.yar.anbar.di.usecase


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.yar.anbar.domain.repository.InvoiceProductRepository
import ir.yar.anbar.domain.repository.InvoiceRepository
import ir.yar.anbar.domain.repository.ProductRepository
import ir.yar.anbar.domain.repository.StockMovementRepository
import ir.yar.anbar.domain.usecase.invoice.DeleteInvoiceUseCase
import ir.yar.anbar.domain.usecase.invoice.GetAllInvoiceUseCase
import ir.yar.anbar.domain.usecase.invoice.GetInvoiceNumberUseCase
import ir.yar.anbar.domain.usecase.invoice.GetInvoiceWithDetailsUseCase
import ir.yar.anbar.domain.usecase.invoice.InitInvoiceWithProductsUseCase
import ir.yar.anbar.domain.usecase.invoice.InsertInvoiceUseCase
import ir.yar.anbar.domain.usecase.product.DecreaseStockUseCase
import ir.yar.anbar.domain.usecase.product.IncreaseStockUseCase
import ir.yar.anbar.domain.usecase.sales.SaveProductSaleSummeryUseCase
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
        productRepository: ProductRepository,
        saveProductSaleSummeryUseCase: SaveProductSaleSummeryUseCase,
        increaseStockUseCase: IncreaseStockUseCase,
        decreaseStockUseCase: DecreaseStockUseCase
    ): InsertInvoiceUseCase = InsertInvoiceUseCase(
        invoiceRepository,
        stockMovementRepository,
        productRepository,
        invoiceProductRepository,
        saveProductSaleSummeryUseCase,
        increaseStockUseCase,
        decreaseStockUseCase
    )

    @Provides
    @Singleton
    fun provideDeleteInvoiceUseCase(
        invoiceRepository: InvoiceRepository
    ): DeleteInvoiceUseCase = DeleteInvoiceUseCase(invoiceRepository)





}