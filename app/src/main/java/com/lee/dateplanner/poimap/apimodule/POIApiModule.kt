package com.lee.dateplanner.poimap.apimodule

import com.lee.dateplanner.BuildConfig
import com.lee.dateplanner.festival.network.FESTIVAL_ADDRESS
import com.lee.dateplanner.poimap.POIRepository
import com.lee.dateplanner.poimap.network.POIRetrofitService
import com.lee.dateplanner.poimap.network.POI_ADDRESS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object POIApiModule {

    @Provides
    fun provideBaseUrl() = POI_ADDRESS

    @Singleton
    @Provides
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
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(provideBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): POIRetrofitService {
        return retrofit.create(POIRetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun provideMainRepository(apiService:POIRetrofitService)= POIRepository(apiService)
}