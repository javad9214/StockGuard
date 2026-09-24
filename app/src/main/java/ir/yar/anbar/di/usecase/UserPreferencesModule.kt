package ir.yar.anbar.di.usecase


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.yar.anbar.domain.repository.UserPreferencesRepository
import ir.yar.anbar.domain.usecase.userpreferences.GetDefaultUnitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.GetStockRunoutLimitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.GetVisibleUnitsUseCase
import ir.yar.anbar.domain.usecase.userpreferences.SaveDefaultUnitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.SaveStockRunoutLimitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.SaveVisibleUnitsUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserPreferencesModule {

    @Provides
    @Singleton
    fun provideGetStockRunoutLimitUseCase(
        repository: UserPreferencesRepository
    ): GetStockRunoutLimitUseCase = GetStockRunoutLimitUseCase(repository)


    @Provides
    @Singleton
    fun provideSaveStockRunoutLimitUseCase(
        repository: UserPreferencesRepository
    ): SaveStockRunoutLimitUseCase = SaveStockRunoutLimitUseCase(repository)

    @Provides
    @Singleton
    fun provideGetDefaultUnitUseCase(
        repository: UserPreferencesRepository
    ): GetDefaultUnitUseCase = GetDefaultUnitUseCase(repository)

    @Provides
    @Singleton
    fun provideSaveDefaultUnitUseCase(
        repository: UserPreferencesRepository
    ): SaveDefaultUnitUseCase = SaveDefaultUnitUseCase(repository)

    @Provides
    @Singleton
    fun provideGetVisibleUnitsUseCase(
        repository: UserPreferencesRepository
    ): GetVisibleUnitsUseCase = GetVisibleUnitsUseCase(repository)

    @Provides
    @Singleton
    fun provideSaveVisibleUnitsUseCase(
        repository: UserPreferencesRepository
    ): SaveVisibleUnitsUseCase = SaveVisibleUnitsUseCase(repository)
}