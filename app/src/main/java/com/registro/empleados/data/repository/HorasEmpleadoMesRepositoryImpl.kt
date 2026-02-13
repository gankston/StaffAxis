package com.registro.empleados.data.repository

import com.registro.empleados.data.local.database.AppDatabase
import com.registro.empleados.data.local.dao.HorasEmpleadoMesDao
import com.registro.empleados.data.local.mapper.HorasEmpleadoMesMapper
import com.registro.empleados.domain.model.HorasEmpleadoMes
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio para horas trabajadas por empleado por mes.
 */
@Singleton
class HorasEmpleadoMesRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : HorasEmpleadoMesRepository {
    
    private val horasDao: HorasEmpleadoMesDao = database.horasEmpleadoMesDao()

    override suspend fun getHorasByLegajo(legajo: String): Flow<List<HorasEmpleadoMes>> {
        return horasDao.getHorasByLegajo(legajo).map { entities ->
            HorasEmpleadoMesMapper.toDomainList(entities)
        }
    }

    override suspend fun getHorasByLegajoAndMes(legajo: String, año: Int, mes: Int): HorasEmpleadoMes? {
        val entity = horasDao.getHorasByLegajoAndMes(legajo, año, mes)
        return entity?.let { HorasEmpleadoMesMapper.toDomain(it) }
    }

    override suspend fun getHorasByMes(año: Int, mes: Int): Flow<List<HorasEmpleadoMes>> {
        return horasDao.getHorasByMes(año, mes).map { entities ->
            HorasEmpleadoMesMapper.toDomainList(entities)
        }
    }

    override suspend fun getHorasByAño(año: Int): Flow<List<HorasEmpleadoMes>> {
        return horasDao.getHorasByAño(año).map { entities ->
            HorasEmpleadoMesMapper.toDomainList(entities)
        }
    }

    override suspend fun insertOrUpdateHoras(horas: HorasEmpleadoMes) {
        val entity = HorasEmpleadoMesMapper.toEntity(horas)
        horasDao.insertHorasEmpleadoMes(entity)
    }

    override suspend fun deleteHorasByLegajoAndMes(legajo: String, año: Int, mes: Int) {
        horasDao.deleteHorasByLegajoAndMes(legajo, año, mes)
    }
}
