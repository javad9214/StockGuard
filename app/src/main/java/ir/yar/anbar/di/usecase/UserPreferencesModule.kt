package ir.yar.anbar.di.usecase


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.yar.anbar.domain.repository.UserPreferencesRepository
import ir.yar.anbar.domain.usecase.userpreferences.GetStockRunoutLimitUseCase
import ir.yar.anbar.domain.usecase.userpreferences.SaveStockRunoutLimitUseCase
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
}