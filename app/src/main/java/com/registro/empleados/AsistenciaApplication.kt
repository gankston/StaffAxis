package com.registro.empleados

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.registro.empleados.data.device.DeviceIdentityManager
import com.registro.empleados.worker.WorkManagerInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
    }
}
