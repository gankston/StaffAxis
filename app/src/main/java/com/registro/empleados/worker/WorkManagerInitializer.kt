package com.registro.empleados.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inicializador del WorkManager para tareas en segundo plano.
 * Configura la sincronización automática de feriados.
 */
@Singleton
class WorkManagerInitializer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Inicializa todas las tareas programadas del WorkManager.
     */
    fun initialize() {
        setupFeriadosSyncWork()
    }
    
    /**
     * Configura la sincronización automática de feriados.
     * Se ejecuta cada 30 días cuando hay conexión a internet.
     */
    private fun setupFeriadosSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val feriadosSyncWork = PeriodicWorkRequestBuilder<FeriadosSyncWorker>(
            30, // Intervalo de 30 días
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .addTag(FeriadosSyncWorker.WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            FeriadosSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            feriadosSyncWork
        )
    }
    
    /**
     * Cancela la sincronización automática de feriados.
     */
    fun cancelFeriadosSyncWork() {
        WorkManager.getInstance(context).cancelUniqueWork(FeriadosSyncWorker.WORK_NAME)
    }
    
    /**
     * Ejecuta inmediatamente la sincronización de feriados.
     * Útil para sincronización manual.
     */
    fun triggerFeriadosSyncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val immediateWork = PeriodicWorkRequestBuilder<FeriadosSyncWorker>(
            1, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(FeriadosSyncWorker.WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueue(immediateWork)
    }
}
