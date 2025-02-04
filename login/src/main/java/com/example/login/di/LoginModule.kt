package com.example.login.di

import com.example.login.data.repository.LoginRepositoryImpl
import com.example.login.domain.repository.LoginRepository
import com.example.login.domain.usecase.LoginUseCase
import dagger.Binds
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
    fun provideLoginUseCase(loginRepository: LoginRepository): LoginUseCase =
        LoginUseCase(loginRepository)

}