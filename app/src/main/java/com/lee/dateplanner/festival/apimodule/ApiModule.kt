package com.lee.dateplanner.festival.apimodule

import com.lee.dateplanner.BuildConfig
import com.lee.dateplanner.festival.FestivalRepository
import com.lee.dateplanner.festival.network.FESTIVAL_ADDRESS
import com.lee.dateplanner.festival.network.FestivalRetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TypeFestival

    @Provides
    fun provideBaseUrl() = FESTIVAL_ADDRESS

    @Singleton
    @Provides
    @TypeFestival
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    @TypeFestival
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(provideBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @TypeFestival
    fun provideApiService(): FestivalRetrofitService =
        Retrofit.Builder()
            .baseUrl(FESTIVAL_ADDRESS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FestivalRetrofitService::class.java)


    @Singleton
    @Provides
    @TypeFestival
    fun provideMainRepository(apiService:FestivalRetrofitService)= FestivalRepository(apiService)
}