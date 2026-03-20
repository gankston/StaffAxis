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
        Log.i("StaffAxis", "BASE_URL=${BuildConfig.BASE_URL}")
        Log.i("StaffAxis", "BUILD_TYPE=${BuildConfig.BUILD_TYPE} VERSION=${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
        Log.i("StaffAxis", "DEVICE MODEL=${android.os.Build.MODEL} SDK=${android.os.Build.VERSION.SDK_INT}")

        // Inicializar WorkManager para tareas en segundo plano
        workManagerInitializer.initialize()
        
        // Al primer arranque: generar device_id si no existe
        appScope.launch {
            deviceIdentityManager.ensureDeviceId()
        }

        // Ping backend para confirmar conectividad al abrir
        appScope.launch {
            try {
                Log.i("StaffAxis", "GET_SECTORS start (ping)")
                val response = withContext(Dispatchers.IO) {
                    sectorsApiService.getSectors()
                }
                val ct = response.headers().get("Content-Type") ?: ""
                Log.i("StaffAxis", "GET_SECTORS ok status=${response.code()} content-type=$ct")
            } catch (e: Exception) {
                Log.e("StaffAxis", "GET_SECTORS fail type=${e.javaClass.simpleName} msg=${e.message}", e)
            }
        }
    }
}
