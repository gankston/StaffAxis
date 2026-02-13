package com.registro.empleados

import android.app.Application
import com.registro.empleados.worker.WorkManagerInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Clase Application principal de la aplicación.
 * Se encarga de inicializar componentes globales como Hilt y WorkManager.
 */
@HiltAndroidApp
class AsistenciaApplication : Application() {

    @Inject
    lateinit var workManagerInitializer: WorkManagerInitializer

    override fun onCreate() {
        super.onCreate()
        
        // Inicializar WorkManager para tareas en segundo plano
        workManagerInitializer.initialize()
    }
}
