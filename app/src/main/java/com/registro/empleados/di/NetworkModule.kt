package com.registro.empleados.di

import android.util.Log
import com.registro.empleados.BuildConfig
import com.registro.empleados.data.local.preferences.DevicePrefs
import com.registro.empleados.data.remote.api.AuthApiService
import com.registro.empleados.data.remote.api.FeriadosApiService
import com.registro.empleados.data.remote.api.SectorsApiService
import com.registro.empleados.data.remote.api.SubmissionsApiService
import com.registro.empleados.data.remote.datasource.FeriadosRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo de Dagger Hilt para la configuración de red.
 * Configura Retrofit, OkHttp y servicios de API.
 * - Retrofit principal usa BuildConfig.BASE_URL (backend StaffAxis)
 * - Interceptores: User-Agent, Authorization: Bearer (device_token) cuando exista
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val FERIADOS_BASE_URL = "https://nolaborables.com.ar/"
    private const val USER_AGENT = "StaffAxis-Android/1.0"

    /**
     * Proporciona el cliente OkHttp con User-Agent y Auth (Bearer device_token cuando exista).
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(devicePrefs: DevicePrefs): OkHttpClient {
        val userAgentInterceptor = Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", USER_AGENT)
                    .build()
            )
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            val path = request.url.encodedPath
            val isStaffAxisApi = request.url.host.contains("staffaxis")
            val needsToken = isStaffAxisApi && path.startsWith("/api/") && !path.startsWith("/api/auth/")
            val token = if (needsToken) devicePrefs.getDeviceTokenSync() else null
            val hasToken = !token.isNullOrBlank()
            if (needsToken) {
                Log.i("StaffAxis", "device_token present=$hasToken path=$path")
            }
            val newRequest = if (needsToken && hasToken) {
                request.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("X-Device-Token", token!!)
                    .build()
            } else {
                request
            }
            if (path.contains("/api/submissions")) {
                Log.i("StaffAxis", "HTTP -> " + newRequest.url + " headers=" + newRequest.headers)
            }
            chain.proceed(newRequest)
        }

        val urlLoggingInterceptor = Interceptor { chain ->
            val request = chain.request()
            Log.i("StaffAxis", "HTTP " + request.method + " " + request.url)
            chain.proceed(request)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(urlLoggingInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Proporciona Retrofit para el backend API (BuildConfig.BASE_URL).
     */
    @Provides
    @Singleton
    @Named("api")
    fun provideApiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Proporciona Retrofit para la API de feriados.
     */
    @Provides
    @Singleton
    @Named("feriados")
    fun provideFeriadosRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FERIADOS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(
        @Named("api") retrofit: Retrofit
    ): AuthApiService {
        return createRetrofitService(retrofit, AuthApiService::class.java, "AuthApiService")
    }

    @Provides
    @Singleton
    fun provideSectorsApiService(
        @Named("api") retrofit: Retrofit
    ): SectorsApiService {
        return createRetrofitService(retrofit, SectorsApiService::class.java, "SectorsApiService")
    }

    private fun <T> createRetrofitService(retrofit: Retrofit, serviceClass: Class<T>, name: String): T {
        return try {
            retrofit.create(serviceClass)
        } catch (e: Exception) {
            Log.e("StaffAxis", "Retrofit.create failed for interface=$name", e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideSubmissionsApiService(
        @Named("api") retrofit: Retrofit
    ): SubmissionsApiService {
        return createRetrofitService(retrofit, SubmissionsApiService::class.java, "SubmissionsApiService")
    }

    /**
     * Proporciona el servicio de API de feriados.
     */
    @Provides
    @Singleton
    fun provideFeriadosApiService(
        @Named("feriados") retrofit: Retrofit
    ): FeriadosApiService {
        return createRetrofitService(retrofit, FeriadosApiService::class.java, "FeriadosApiService")
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
