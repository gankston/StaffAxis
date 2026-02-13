package com.registro.empleados.data.repository

import com.registro.empleados.data.local.database.AppDatabase
import com.registro.empleados.data.local.dao.RegistroAsistenciaDao
import com.registro.empleados.data.local.mapper.RegistroAsistenciaMapper
import com.registro.empleados.domain.model.RegistroAsistencia
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de registros de asistencia.
 * Actualizada para nueva estructura: solo horas trabajadas manuales (4, 8 o 12 horas).
 */
@Singleton
class RegistroAsistenciaRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : RegistroAsistenciaRepository {
    
    private val registroDao: RegistroAsistenciaDao = database.registroAsistenciaDao()

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
        return registroDao.insertRegistro(entity)
    }

    override suspend fun updateRegistro(registro: RegistroAsistencia) {
        val entity = RegistroAsistenciaMapper.toEntity(registro)
        registroDao.updateRegistro(entity)
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