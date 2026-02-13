package com.registro.empleados.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.registro.empleados.domain.usecase.feriados.SincronizarFeriadosUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

/**
 * Worker para sincronización automática de feriados argentinos.
 * Se ejecuta periódicamente para mantener actualizados los feriados.
 */
@HiltWorker
class FeriadosSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val sincronizarFeriadosUseCase: SincronizarFeriadosUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val añoActual = LocalDate.now().year
            val añoSiguiente = añoActual + 1
            
            // Sincronizar feriados del año actual
            val resultadoActual = sincronizarFeriadosUseCase(añoActual)
            
            // Sincronizar feriados del año siguiente (para planificación)
            val resultadoSiguiente = sincronizarFeriadosUseCase(añoSiguiente)
            
            // Verificar si ambos fueron exitosos
            if (resultadoActual.exito && resultadoSiguiente.exito) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "feriados_sync_worker"
        const val WORK_TAG = "feriados_sync"
    }
}
