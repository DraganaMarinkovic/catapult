package com.example.catapult.di

import com.example.catapult.BuildConfig
import com.example.catapult.network.CatApi
import com.example.catapult.network.LeaderboardApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor {
                    chain ->
                val original = chain.request()
                val requestWithKey = original.newBuilder()
                    .addHeader("x-api-key", BuildConfig.CAT_API_KEY)
                    .build()
                chain.proceed(requestWithKey)
            }
            .build()

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @Provides @Singleton
    @Named("CatRetrofit")
    fun provideCatRetrofit(
        json: Json,
        client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides @Singleton
    fun provideCatApi(
        @Named("CatRetrofit") retrofit: Retrofit
    ): CatApi = retrofit.create(CatApi::class.java)


    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @Provides @Singleton
    @Named("RmaRetrofit")
    fun provideRmaRetrofit(
        json: Json,
        client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://rma.finlab.rs/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides @Singleton
    fun provideLeaderboardApi(
        @Named("RmaRetrofit") retrofit: Retrofit
    ): LeaderboardApi = retrofit.create(LeaderboardApi::class.java)
}
