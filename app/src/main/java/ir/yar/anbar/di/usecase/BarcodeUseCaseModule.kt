package ir.yar.anbar.di.usecase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.yar.anbar.domain.repository.BarcodeLookupRepository
import ir.yar.anbar.domain.usecase.barcode.LookupBarcodeUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BarcodeUseCaseModule {

    @Provides
    @Singleton
    fun provideLookupBarcodeUseCase(
        repository: BarcodeLookupRepository
    ): LookupBarcodeUseCase = LookupBarcodeUseCase(repository)
}
