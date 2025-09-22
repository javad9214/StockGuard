package com.example.composetrainer.di

import com.example.composetrainer.domain.repository.ProductRepository
import com.example.composetrainer.domain.repository.ServerMainProductRepository
import com.example.composetrainer.domain.usecase.product.AddProductUseCase
import com.example.composetrainer.domain.usecase.product.CheckProductStockUseCase
import com.example.composetrainer.domain.usecase.product.DecreaseStockUseCase
import com.example.composetrainer.domain.usecase.product.DeleteProductUseCase
import com.example.composetrainer.domain.usecase.product.EditProductUseCase
import com.example.composetrainer.domain.usecase.product.GetAllProductUseCase
import com.example.composetrainer.domain.usecase.product.GetProductByBarcodeUseCase
import com.example.composetrainer.domain.usecase.product.GetProductByQueryUseCase
import com.example.composetrainer.domain.usecase.product.GetProductsByIDsUseCase
import com.example.composetrainer.domain.usecase.product.IncreaseStockUseCase
import com.example.composetrainer.domain.usecase.servermainproduct.AddNewProductToMainServerUseCase
import com.example.composetrainer.domain.usecase.servermainproduct.GetAllMainProductsUseCase
import com.example.composetrainer.domain.usecase.servermainproduct.GetSearchedMainProductsUseCase
import com.example.composetrainer.ui.viewmodels.MainProductsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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