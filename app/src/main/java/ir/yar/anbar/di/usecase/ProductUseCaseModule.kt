package ir.yar.anbar.di.usecase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.yar.anbar.domain.repository.ProductRepository
import ir.yar.anbar.domain.repository.ServerMainProductRepository
import ir.yar.anbar.domain.usecase.product.AddProductUseCase
import ir.yar.anbar.domain.usecase.product.CheckProductStockUseCase
import ir.yar.anbar.domain.usecase.product.DecreaseStockUseCase
import ir.yar.anbar.domain.usecase.product.DeleteProductUseCase
import ir.yar.anbar.domain.usecase.product.EditProductUseCase
import ir.yar.anbar.domain.usecase.product.GetAllProductUseCase
import ir.yar.anbar.domain.usecase.product.GetProductByBarcodeUseCase
import ir.yar.anbar.domain.usecase.product.GetProductByQueryUseCase
import ir.yar.anbar.domain.usecase.product.GetProductsByIDsUseCase
import ir.yar.anbar.domain.usecase.product.IncreaseStockUseCase
import ir.yar.anbar.domain.usecase.servermainproduct.AddNewProductToMainServerUseCase
import ir.yar.anbar.domain.usecase.servermainproduct.GetAllMainProductsUseCase
import ir.yar.anbar.domain.usecase.servermainproduct.GetSearchedMainProductsUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductUseCaseModule {

    @Provides
    @Singleton
    fun provideAddProductUseCase(
        repository: ProductRepository
    ): AddProductUseCase = AddProductUseCase(repository)

    @Provides
    @Singleton
    fun provideGetProductsByQueryUseCase(
        repository: ProductRepository
    ): GetProductByQueryUseCase = GetProductByQueryUseCase(repository)

    @Provides
    @Singleton
    fun provideCheckProductStockUseCase(
        repository: ProductRepository
    ): CheckProductStockUseCase = CheckProductStockUseCase(repository)

    @Provides
    @Singleton
    fun provideDecreaseStockUseCase(
        repository: ProductRepository
    ): DecreaseStockUseCase = DecreaseStockUseCase(repository)

    @Provides
    @Singleton
    fun provideIncreaseStockUseCase(
        repository: ProductRepository
    ): IncreaseStockUseCase = IncreaseStockUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteProductUseCase(
        repository: ProductRepository
    ): DeleteProductUseCase = DeleteProductUseCase(repository)

    @Provides
    @Singleton
    fun provideEditProductUseCase(
        repository: ProductRepository
    ): EditProductUseCase = EditProductUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAllProductsUseCase(
        repository: ProductRepository
    ): GetAllProductUseCase = GetAllProductUseCase(repository)

    @Provides
    @Singleton
    fun provideGetProductByBarcodeUseCase(
        repository: ProductRepository
    ): GetProductByBarcodeUseCase = GetProductByBarcodeUseCase(repository)

    @Provides
    @Singleton
    fun provideGetProductsByIDsUseCase(
        repository: ProductRepository
    ): GetProductsByIDsUseCase = GetProductsByIDsUseCase(repository)


    @Provides
    @Singleton
    fun provideGetAllMainProductsUseCase(
        repository: ServerMainProductRepository
    ): GetAllMainProductsUseCase = GetAllMainProductsUseCase(repository)


    @Provides
    @Singleton
    fun provideGetSearchedMainProductsUseCase(
        repository: ServerMainProductRepository
    ): GetSearchedMainProductsUseCase = GetSearchedMainProductsUseCase(repository)

    @Provides
    @Singleton
    fun provideAddNewProductToMainServerUseCase(
        repository: ServerMainProductRepository
    ): AddNewProductToMainServerUseCase = AddNewProductToMainServerUseCase(repository)
}