package com.registro.empleados.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.registro.empleados.BuildConfig
import com.registro.empleados.data.local.preferences.DevicePrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import com.registro.empleados.data.remote.api.AuthApiService
import com.registro.empleados.data.remote.api.FeriadosApiService
import com.registro.empleados.data.remote.api.SectorsApiService
import com.registro.empleados.data.remote.api.EmployeesApiService
import com.registro.empleados.data.remote.api.SubmissionsApiService
import com.registro.empleados.data.remote.api.AusenciasApiService
import com.registro.empleados.data.remote.datasource.FeriadosRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLHandshakeException

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val FERIADOS_BASE_URL = "https://nolaborables.com.ar/"
    private val USER_AGENT = "StaffAxis-Android/${BuildConfig.VERSION_NAME}"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        devicePrefs: DevicePrefs
    ): OkHttpClient {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        fun buildCapabilitiesString(): String {
            val cm = connectivityManager ?: return "activeNetwork=null"
            val net = cm.activeNetwork ?: return "activeNetwork=null"
            val caps = cm.getNetworkCapabilities(net) ?: return "activeNetwork=ok caps=null"
            val wifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            val cellular = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            val internet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val validated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            val transport = when {
                wifi -> "wifi"
                cellular -> "cellular"
                else -> "other"
            }
            return "validated=$validated transport=$transport internet=$internet"
        }

        val networkDiagInterceptor = Interceptor { chain ->
            val request = chain.request()
            val url = request.url.toString()
            val host = request.url.host
            val capsStr = buildCapabilitiesString()
            val dnsResult = try {
                val ips = InetAddress.getAllByName(host).map { it.hostAddress }
                "dns=[${ips.joinToString(",")}]"
            } catch (e: UnknownHostException) {
                "dns=FAIL(${e.message})"
            } catch (e: Exception) {
                "dns=FAIL(${e.javaClass.simpleName})"
            }
            Log.i("StaffAxis", "NETWORK_DIAG url=$url $capsStr $dnsResult")
            try {
                return@Interceptor chain.proceed(request)
            } catch (e: IOException) {
                Log.e("StaffAxis", "NETWORK_DIAG fail type=${e.javaClass.simpleName} msg=${e.message}", e)
                Log.i("StaffAxis", "NETWORK_DIAG post-fail ${buildCapabilitiesString()}")
                throw e
            } catch (e: UnknownHostException) {
                Log.e("StaffAxis", "NETWORK_DIAG fail type=${e.javaClass.simpleName} msg=${e.message}", e)
                Log.i("StaffAxis", "NETWORK_DIAG post-fail ${buildCapabilitiesString()}")
                throw e
            } catch (e: SSLHandshakeException) {
                Log.e("StaffAxis", "NETWORK_DIAG fail type=${e.javaClass.simpleName} msg=${e.message}", e)
                Log.i("StaffAxis", "NETWORK_DIAG post-fail ${buildCapabilitiesString()}")
                throw e
            }
        }

        val userAgentInterceptor = Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", USER_AGENT)
                    .build()
            )
        }

        val sectorsPeekInterceptor = Interceptor { chain ->
            val request = chain.request()
            val path = request.url.encodedPath
            if (!path.endsWith("/api/sectors")) {
                return@Interceptor chain.proceed(request)
            }
            val fullUrl = request.url.toString()
            val ua = request.header("User-Agent") ?: ""
            val accept = request.header("Accept") ?: ""
            val acceptEncoding = request.header("Accept-Encoding") ?: ""
            Log.i("StaffAxis", "SECTORS_REQ url=$fullUrl headers(user-agent=$ua accept=$accept accept-encoding=$acceptEncoding)")
            val response = chain.proceed(request)
            val status = response.code
            val ct = response.header("Content-Type") ?: ""
            val len = response.header("Content-Length") ?: "?"
            Log.i("StaffAxis", "SECTORS_RES status=$status ct=$ct len=$len")
            val peek = try {
                response.peekBody(4096).string()
            } catch (e: Exception) {
                "peek_error: ${e.message}"
            }
            Log.i("StaffAxis", "SECTORS_PEEK=${peek.take(500)}")
            response
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            val path = request.url.encodedPath
            val isStaffAxisApi = request.url.host.contains("staffaxis")
            val needsToken = isStaffAxisApi && path.startsWith("/api/") && !path.startsWith("/api/auth/") && !path.contains("/sectors")
            val token = if (needsToken) devicePrefs.getDeviceTokenSync() else null
            val newRequest = if (needsToken && !token.isNullOrBlank()) {
                request.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("X-Device-Token", token)
                    .build()
            } else {
                request
            }
            chain.proceed(newRequest)
        }

        // Forzar peticiones sin caché HTTP (siempre datos frescos)
        val noCacheInterceptor = Interceptor { chain ->
            val original = chain.request()
            val newRequest = original.newBuilder()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .build()
            chain.proceed(newRequest)
        }

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("NETWORK_DEBUG", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(networkDiagInterceptor)
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(sectorsPeekInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(noCacheInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideTolerantGson(): com.google.gson.Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()
    }

    @Provides
    @Singleton
    @Named("api")
    fun provideApiRetrofit(okHttpClient: OkHttpClient, gson: com.google.gson.Gson): Retrofit {
        Log.i("StaffAxis", "RETROFIT_API baseUrl=${BuildConfig.BASE_URL}")
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
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

    @Provides
    @Singleton
    fun provideEmployeesApiService(
        @Named("api") retrofit: Retrofit
    ): EmployeesApiService {
        return createRetrofitService(retrofit, EmployeesApiService::class.java, "EmployeesApiService")
    }

    @Provides
    @Singleton
    fun provideAusenciasApiService(
        @Named("api") retrofit: Retrofit
    ): AusenciasApiService {
        return createRetrofitService(retrofit, AusenciasApiService::class.java, "AusenciasApiService")
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
