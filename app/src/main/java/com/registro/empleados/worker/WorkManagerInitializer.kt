package com.registro.empleados.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
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
        triggerPullApprovedNow(staggerSeconds = 30)
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
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
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
     * initialDelay 10s para no spamear al inicio.
     */
    private fun triggerPushOutboxNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()
        
        val oneTimeWork = OneTimeWorkRequestBuilder<PushOutboxWorker>()
            .setConstraints(constraints)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                PushOutboxWorker.BACKOFF_DELAY,
                PushOutboxWorker.BACKOFF_UNIT
            )
            .addTag(PushOutboxWorker.WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            PushOutboxWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWork
        )
    }
    
    /**
     * Configura el pull de approved cada 15 min.
     */
    private fun setupPullApprovedWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
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
     * @param staggerSeconds retraso inicial para no ejecutar junto con PushOutbox (evitar tormenta).
     */
    private fun triggerPullApprovedNow(staggerSeconds: Long = 0) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()
        
        val builder = OneTimeWorkRequestBuilder<PullApprovedWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                PullApprovedWorker.BACKOFF_DELAY,
                PullApprovedWorker.BACKOFF_UNIT
            )
            .addTag(PullApprovedWorker.WORK_TAG)
        if (staggerSeconds > 0) {
            builder.setInitialDelay(staggerSeconds, TimeUnit.SECONDS)
        }
        val oneTimeWork = builder.build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            PullApprovedWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWork
        )
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
