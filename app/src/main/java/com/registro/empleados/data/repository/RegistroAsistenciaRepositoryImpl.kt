package com.registro.empleados.data.repository

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.registro.empleados.data.local.database.AppDatabase
import com.registro.empleados.data.local.dao.OutboxSubmissionDao
import com.registro.empleados.data.local.dao.RegistroAsistenciaDao
import com.registro.empleados.data.local.entity.OutboxSubmissionEntity
import com.registro.empleados.data.local.mapper.RegistroAsistenciaMapper
import com.registro.empleados.domain.model.RegistroAsistencia
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import com.registro.empleados.worker.PushOutboxWorker
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de registros de asistencia.
 * Actualizada para nueva estructura: solo horas trabajadas manuales (4, 8 o 12 horas).
 */
@Singleton
class RegistroAsistenciaRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val workManager: WorkManager
) : RegistroAsistenciaRepository {
    
    private val registroDao: RegistroAsistenciaDao = database.registroAsistenciaDao()
    private val outboxDao: OutboxSubmissionDao = database.outboxSubmissionDao()

    override suspend fun getRegistrosByLegajoYRango(
        legajo: String, 
        fechaInicio: String, 
        fechaFin: String
    ): List<RegistroAsistencia> {
        val entities = registroDao.getRegistrosByLegajoYRango(legajo, fechaInicio, fechaFin)
        return RegistroAsistenciaMapper.toDomainList(entities)
    }

    override suspend fun getRegistrosByRango(
        fechaInicio: String, 
        fechaFin: String
    ): List<RegistroAsistencia> {
        val entities = registroDao.getRegistrosByRango(fechaInicio, fechaFin)
        return RegistroAsistenciaMapper.toDomainList(entities)
    }

    override suspend fun getRegistroByLegajoYFecha(
        legajo: String, 
        fecha: String
    ): RegistroAsistencia? {
        val entity = registroDao.getRegistroByLegajoYFecha(legajo, fecha)
        return entity?.let { RegistroAsistenciaMapper.toDomain(it) }
    }

    override suspend fun insertRegistro(registro: RegistroAsistencia): Long {
        val entity = RegistroAsistenciaMapper.toEntity(registro)
        val id = registroDao.insertRegistro(entity)
        addToOutboxIfNeeded(registro)
        schedulePushOutbox()
        return id
    }

    override suspend fun updateRegistro(registro: RegistroAsistencia) {
        val entity = RegistroAsistenciaMapper.toEntity(registro)
        registroDao.updateRegistro(entity)
        addToOutboxIfNeeded(registro)
        schedulePushOutbox()
    }

    private suspend fun addToOutboxIfNeeded(registro: RegistroAsistencia) {
        val employeeId = registro.legajoEmpleado
        val date = registro.fecha
        val minutesWorked = registro.horasTrabajadas * 60
        val checkIn = null
        val checkOut = null
        val checkInOrEmpty = checkIn ?: ""
        val checkOutOrEmpty = checkOut ?: ""
        val minutesWorkedOrSentinel = minutesWorked
        if (outboxDao.countPendingWithSameKey(
                employeeId,
                date,
                checkInOrEmpty,
                checkOutOrEmpty,
                minutesWorkedOrSentinel
            ) > 0
        ) return
        val outbox = OutboxSubmissionEntity(
            id = UUID.randomUUID().toString(),
            employeeId = employeeId,
            date = date,
            minutesWorked = minutesWorked,
            checkIn = checkIn,
            checkOut = checkOut,
            notes = registro.observaciones,
            createdAt = System.currentTimeMillis(),
            attempts = 0,
            lastError = null,
            status = "pending"
        )
        outboxDao.insert(outbox)
    }

    private fun schedulePushOutbox() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequestBuilder<PushOutboxWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, PushOutboxWorker.BACKOFF_DELAY, PushOutboxWorker.BACKOFF_UNIT)
            .addTag(PushOutboxWorker.WORK_TAG)
            .build()
        workManager.enqueue(work)
    }

    override suspend fun deleteRegistro(id: Long) {
        registroDao.deleteRegistro(id)
    }

    override suspend fun getTotalHorasByLegajoYPeriodo(
        legajo: String, 
        fechaInicio: String, 
        fechaFin: String
    ): Int? {
        return registroDao.getTotalHorasByLegajoYPeriodo(legajo, fechaInicio, fechaFin)
    }

    override suspend fun getAllRegistros(): kotlinx.coroutines.flow.Flow<List<RegistroAsistencia>> {
        return registroDao.getAllRegistros().map { entities ->
            RegistroAsistenciaMapper.toDomainList(entities)
        }
    }

    override suspend fun deleteAllRegistros() {
        registroDao.deleteAllRegistros()
    }

    override suspend fun getRegistrosByLegajoAndFecha(
        legajo: String,
        fecha: LocalDate
    ): List<RegistroAsistencia> {
        val entities = registroDao.getRegistrosByLegajoAndFecha(legajo, fecha)
        return RegistroAsistenciaMapper.toDomainList(entities)
    }
}