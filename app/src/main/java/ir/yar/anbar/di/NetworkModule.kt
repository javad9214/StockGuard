package ir.yar.anbar.di

import ir.yar.anbar.data.remote.api.ApiConstants
import ir.yar.anbar.data.remote.api.ApiServiceMainProduct
import ir.yar.anbar.data.remote.api.ApiServiceVersion
import ir.yar.anbar.data.remote.interceptor.AuthInterceptor
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Change to your preferred log level
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)  // Connection timeout
            .readTimeout(300, TimeUnit.SECONDS)     // Read timeout
            .writeTimeout(300, TimeUnit.SECONDS)    // Write timeout
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(okHttpClient: OkHttpClient): ApiServiceMainProduct {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiServiceMainProduct::class.java)
    }

    @Singleton
    @Provides
    fun provideApiServiceVersion(okHttpClient: OkHttpClient): ApiServiceVersion {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiServiceVersion::class.java)
    }
}