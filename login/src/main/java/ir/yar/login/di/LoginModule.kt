package ir.yar.login.di

import ir.yar.login.data.remote.api.ApiAuthService
import ir.yar.login.data.repository.AuthRepositoryImpl
import ir.yar.login.data.repository.TokenManager
import ir.yar.login.domain.repository.AuthRepository
import ir.yar.login.domain.usecase.LoginUseCase
import ir.yar.login.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Singleton
    @Provides
    fun provideAuthRepository(
        apiService: ApiAuthService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, tokenManager)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase =
        LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase =
        RegisterUseCase(authRepository)
}