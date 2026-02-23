package ir.yar.anbar.di.usecase

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.yar.anbar.data.local.datastore.userPreferencesDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // Provide DataStore<Preferences> instance
    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context) =
        context.userPreferencesDataStore
}