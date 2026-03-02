package com.registro.empleados

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.registro.empleados.data.device.DeviceIdentityManager
import com.registro.empleados.data.remote.api.SectorsApiService
import com.registro.empleados.worker.WorkManagerInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Clase Application principal de la aplicación.
 * Se encarga de inicializar componentes globales como Hilt y WorkManager.
 */
@HiltAndroidApp
class AsistenciaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    @Inject
    lateinit var workManagerInitializer: WorkManagerInitializer

    @Inject
    lateinit var deviceIdentityManager: DeviceIdentityManager

    @Inject
    lateinit var sectorsApiService: SectorsApiService

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Log.i("StaffAxis", "BASE_URL=" + BuildConfig.BASE_URL)
        Log.i("StaffAxis", "BUILD_TYPE=" + BuildConfig.BUILD_TYPE + " VERSION=" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")")

        // Inicializar WorkManager para tareas en segundo plano
        workManagerInitializer.initialize()
        
        // Al primer arranque: generar device_id si no existe
        appScope.launch {
            deviceIdentityManager.ensureDeviceId()
        }

        // Ping backend para confirmar conectividad al abrir
        appScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    sectorsApiService.getSectors()
                }
                Log.i("StaffAxis", "GET sectors url=" + response.raw().request.url)
                Log.i("StaffAxis", "GET sectors status=" + response.code())
                val raw = response.body()?.string()
                Log.i("StaffAxis", "GET sectors raw=" + (raw?.take(300) ?: "null"))
                response.errorBody()?.string()?.let { err ->
                    Log.i("StaffAxis", "GET sectors errorBody=" + err.take(300))
                }
            } catch (e: Exception) {
                Log.i("StaffAxis", "PING sectors error=${e.message ?: e.javaClass.simpleName}")
            }
        }
    }
}
