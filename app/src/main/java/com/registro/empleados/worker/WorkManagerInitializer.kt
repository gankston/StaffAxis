package com.registro.empleados.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
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
        setupPushOutboxWork()
        triggerPushOutboxNow()
        setupPullApprovedWork()
        triggerPullApprovedNow()
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
     * Configura el push del outbox cada 15 min (mínimo permitido).
     */
    private fun setupPushOutboxWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val pushOutboxWork = PeriodicWorkRequestBuilder<PushOutboxWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                PushOutboxWorker.BACKOFF_DELAY,
                PushOutboxWorker.BACKOFF_UNIT
            )
            .addTag(PushOutboxWorker.WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PushOutboxWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            pushOutboxWork
        )
    }
    
    /**
     * Dispara el push del outbox al arrancar la app (una vez, cuando haya red).
     */
    private fun triggerPushOutboxNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val oneTimeWork = OneTimeWorkRequestBuilder<PushOutboxWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                PushOutboxWorker.BACKOFF_DELAY,
                PushOutboxWorker.BACKOFF_UNIT
            )
            .addTag(PushOutboxWorker.WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueue(oneTimeWork)
    }
    
    /**
     * Configura el pull de approved cada 15 min.
     */
    private fun setupPullApprovedWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val pullApprovedWork = PeriodicWorkRequestBuilder<PullApprovedWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                PullApprovedWorker.BACKOFF_DELAY,
                PullApprovedWorker.BACKOFF_UNIT
            )
            .addTag(PullApprovedWorker.WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PullApprovedWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            pullApprovedWork
        )
    }
    
    /**
     * Dispara el pull de approved al arrancar la app.
     */
    private fun triggerPullApprovedNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val oneTimeWork = OneTimeWorkRequestBuilder<PullApprovedWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                PullApprovedWorker.BACKOFF_DELAY,
                PullApprovedWorker.BACKOFF_UNIT
            )
            .addTag(PullApprovedWorker.WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueue(oneTimeWork)
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
