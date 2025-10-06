package com.example.login.di

import com.example.login.data.remote.api.ApiAuthService
import com.example.login.data.repository.AuthRepositoryImpl
import com.example.login.data.repository.LoginRepositoryImpl
import com.example.login.domain.repository.AuthRepository
import com.example.login.domain.repository.LoginRepository
import com.example.login.domain.usecase.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideLoginRepository(): LoginRepository = LoginRepositoryImpl()

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiAuthService: ApiAuthService
    ): AuthRepository = AuthRepositoryImpl(
        apiAuthService
    )

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase =
        LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: AuthRepository): LoginUseCase =
        LoginUseCase(authRepository)
}