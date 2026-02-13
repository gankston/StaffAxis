package com.registro.empleados.di

import com.registro.empleados.data.remote.api.FeriadosApiService
import com.registro.empleados.data.remote.datasource.FeriadosRemoteDataSource
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

/**
 * Módulo de Dagger Hilt para la configuración de red.
 * Configura Retrofit, OkHttp y servicios de API.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * URL base para la API de feriados argentinos.
     */
    private const val FERIADOS_BASE_URL = "https://nolaborables.com.ar/"

    /**
     * Proporciona el cliente OkHttp configurado.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Proporciona la instancia de Retrofit configurada.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FERIADOS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Proporciona el servicio de API de feriados.
     */
    @Provides
    @Singleton
    fun provideFeriadosApiService(retrofit: Retrofit): FeriadosApiService {
        return retrofit.create(FeriadosApiService::class.java)
    }

    /**
     * Proporciona la fuente de datos remota para feriados.
     */
    @Provides
    @Singleton
    fun provideFeriadosRemoteDataSource(
        feriadosApiService: FeriadosApiService
    ): FeriadosRemoteDataSource {
        return FeriadosRemoteDataSource(feriadosApiService)
    }
}
