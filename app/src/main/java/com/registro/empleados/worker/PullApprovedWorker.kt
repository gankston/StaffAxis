package com.registro.empleados.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import com.registro.empleados.BuildConfig
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.registro.empleados.data.local.dao.ApprovedAttendanceDao
import com.registro.empleados.data.local.dao.RegistroAsistenciaDao
import com.registro.empleados.data.local.entity.ApprovedAttendanceEntity
import com.registro.empleados.data.local.entity.RegistroAsistenciaEntity
import com.registro.empleados.data.local.preferences.SyncStatePrefs
import com.registro.empleados.data.remote.api.SubmissionsApiService
import com.registro.empleados.data.remote.dto.AttendanceDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Worker que descarga attendances aprobados del backend (GET /approved).
 * Requiere red CONNECTED.
 * Los datos se guardan en approved_attendances y se fusionan en registros_asistencia
 * (la tabla que usa la UI y la exportación).
 */
@HiltWorker
class PullApprovedWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val approvedAttendanceDao: ApprovedAttendanceDao,
    private val registroAsistenciaDao: RegistroAsistenciaDao,
    private val submissionsApi: SubmissionsApiService,
    private val syncStatePrefs: SyncStatePrefs
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val since = syncStatePrefs.getLastApprovedSyncAt()
            val response = submissionsApi.getApproved(since)
            if (!response.isSuccessful) {
                return@withContext Result.retry()
            }
            val body = response.body()
            val attendances = body?.attendances ?: emptyList()
            if (BuildConfig.DEBUG) Log.d(TAG, "pull approved: ${attendances.size} recibidos")
            var maxUpdatedAt = since
            var mergedCount = 0
            for (att in attendances) {
                approvedAttendanceDao.upsert(
                    ApprovedAttendanceEntity(
                        id = att.id,
                        employeeId = att.employeeId,
                        sectorId = att.sectorId,
                        date = att.date,
                        minutesWorked = att.minutesWorked,
                        checkIn = att.checkIn,
                        checkOut = att.checkOut,
                        notes = att.notes,
                        updatedAt = att.updatedAt,
                        isDeleted = if (att.deletedAt != null) 1 else 0
                    )
                )
                if (att.deletedAt != null) {
                    registroAsistenciaDao.deleteRegistroByLegajoYFecha(att.employeeId, att.date)
                } else {
                    upsertRegistroAsistencia(att)
                    mergedCount++
                }
                if (att.updatedAt > maxUpdatedAt) {
                    maxUpdatedAt = att.updatedAt
                }
            }
            if (BuildConfig.DEBUG) Log.d(TAG, "PullApprovedWorker merged $mergedCount records into registros_asistencia")
            val newSyncAt = if (attendances.isEmpty()) {
                System.currentTimeMillis() / 1000
            } else {
                maxUpdatedAt
            }
            syncStatePrefs.setLastApprovedSyncAt(newSyncAt)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun upsertRegistroAsistencia(att: AttendanceDto) {
        val entity = att.toRegistroAsistenciaEntity()
        val existing = registroAsistenciaDao.getRegistroByLegajoYFecha(att.employeeId, att.date)
        if (existing != null) {
            registroAsistenciaDao.updateRegistro(
                entity.copy(id = existing.id, fechaRegistro = existing.fechaRegistro)
            )
        } else {
            registroAsistenciaDao.insertRegistro(entity)
        }
    }

    companion object {
        private const val TAG = "PullApprovedWorker"
        const val WORK_NAME = "pull_approved_worker"
        const val WORK_TAG = "pull_approved"
        val BACKOFF_DELAY = 10L
        val BACKOFF_UNIT = TimeUnit.SECONDS
    }
}

private fun AttendanceDto.toRegistroAsistenciaEntity(): RegistroAsistenciaEntity {
    val horas = when (val m = minutesWorked ?: 480) {
        in 0..300 -> 4
        in 301..600 -> 8
        else -> 12
    }
    return RegistroAsistenciaEntity(
        id = 0,
        idEmpleado = null,
        legajoEmpleado = employeeId,
        fecha = date,
        horasTrabajadas = horas,
        observaciones = notes,
        fechaRegistro = System.currentTimeMillis()
    )
}
