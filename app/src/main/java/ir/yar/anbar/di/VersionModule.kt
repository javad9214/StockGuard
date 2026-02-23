package ir.yar.anbar.di


import ir.yar.anbar.data.remote.api.ApiServiceVersion
import ir.yar.anbar.data.remote.datasource.VersionRemoteDataSource
import ir.yar.anbar.data.repository.VersionRepositoryImpl
import ir.yar.anbar.domain.repository.VersionRepository
import ir.yar.anbar.domain.usecase.versionupdate.CheckAppVersionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VersionModule {


    @Provides
    @Singleton
    fun provideVersionRemoteDataSource(
        apiService: ApiServiceVersion
    ): VersionRemoteDataSource {
        return VersionRemoteDataSource(apiService)
    }


    @Provides
    @Singleton
    fun provideVersionRepository(
        remoteDataSource: VersionRemoteDataSource
    ): VersionRepository {
        return VersionRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideCheckAppVersionUseCase(
        repository: VersionRepository
    ): CheckAppVersionUseCase {
        return CheckAppVersionUseCase(repository)
    }
}