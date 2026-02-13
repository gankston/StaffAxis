package com.registro.empleados.data.repository

import com.registro.empleados.data.local.database.AppDatabase
import com.registro.empleados.data.local.dao.EmpleadoDao
import com.registro.empleados.data.mapper.EmpleadoMapper
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.repository.EmpleadoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de empleados.
 * Maneja la lógica de acceso a datos y conversión entre capas.
 */
@Singleton
class EmpleadoRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : EmpleadoRepository {
    
    private val empleadoDao: EmpleadoDao = database.empleadoDao()

    override suspend fun getAllEmpleadosActivos(): Flow<List<Empleado>> {
        return empleadoDao.getAllEmpleadosActivos().map { entities ->
            EmpleadoMapper.toDomainList(entities)
        }
    }

    override suspend fun getAllEmpleados(): Flow<List<Empleado>> {
        return empleadoDao.getAllEmpleados().map { entities ->
            EmpleadoMapper.toDomainList(entities)
        }
    }

    override suspend fun getEmpleadoByLegajo(legajo: String): Empleado? {
        val entity = empleadoDao.getEmpleadoByLegajo(legajo)
        return entity?.let { EmpleadoMapper.toDomain(it) }
    }

    suspend fun getEmpleadoById(id: Long): Empleado? {
        val entity = empleadoDao.getEmpleadoById(id)
        return entity?.let { EmpleadoMapper.toDomain(it) }
    }

    override suspend fun insertEmpleado(empleado: Empleado) {
        // Verificar si ya existe un empleado con ese legajo (solo si no es null y no es automático)
        if (empleado.legajo != null && !empleado.legajo.startsWith("AUTO_") && existeEmpleadoConLegajo(empleado.legajo)) {
            throw IllegalArgumentException("Ya existe un empleado con el legajo ${empleado.legajo}")
        }
        
        val entity = EmpleadoMapper.toEntity(empleado)
        empleadoDao.insertEmpleado(entity)
    }

    override suspend fun updateEmpleado(empleado: Empleado) {
        android.util.Log.d("EmpleadoRepositoryImpl", "=== REPOSITORIO UPDATE ===")
        android.util.Log.d("EmpleadoRepositoryImpl", "Empleado recibido - ID: ${empleado.id}, Legajo: ${empleado.legajo}, Nombre: ${empleado.nombreCompleto}")
        
        val entity = EmpleadoMapper.toEntity(empleado)
        
        android.util.Log.d("EmpleadoRepositoryImpl", "Entity convertida - ID: ${entity.id}, Legajo: ${entity.legajo}, Nombre: ${entity.nombreCompleto}")
        
        empleadoDao.updateEmpleado(entity)
        
        android.util.Log.d("EmpleadoRepositoryImpl", "✅ DAO update ejecutado")
    }

    override suspend fun darDeBajaEmpleado(legajo: String) {
        empleadoDao.darDeBajaEmpleado(legajo)
    }

    override suspend fun updateEstadoEmpleado(id: Long, activo: Boolean) {
        empleadoDao.updateEstadoEmpleado(id, activo)
    }

    override suspend fun existeEmpleadoConLegajo(legajo: String?): Boolean {
        return legajo != null && empleadoDao.getEmpleadoByLegajo(legajo) != null
    }
    
    override suspend fun buscarEmpleadosPorNombre(nombre: String): Flow<List<Empleado>> {
        return empleadoDao.buscarEmpleadosPorNombre(nombre).map { entities ->
            EmpleadoMapper.toDomainList(entities)
        }
    }
    
    override suspend fun buscarEmpleadosPorApellido(apellido: String): Flow<List<Empleado>> {
        return empleadoDao.buscarEmpleadosPorApellido(apellido).map { entities ->
            EmpleadoMapper.toDomainList(entities)
        }
    }
    
    override suspend fun buscarEmpleadosPorNombreYApellido(nombre: String, apellido: String): Flow<List<Empleado>> {
        return empleadoDao.buscarEmpleadosPorNombreYApellido(nombre).map { entities ->
            EmpleadoMapper.toDomainList(entities)
        }
    }
    
    override suspend fun buscarEmpleados(query: String): Flow<List<Empleado>> {
        return empleadoDao.buscarEmpleados(query).map { entities ->
            EmpleadoMapper.toDomainList(entities)
        }
    }

    override suspend fun deleteEmpleado(legajo: String) {
        empleadoDao.deleteByLegajo(legajo)
    }
    
    override suspend fun quitarLegajoEmpleado(id: Long) {
        empleadoDao.quitarLegajoEmpleado(id)
    }

    override suspend fun deleteAllEmpleados() {
        empleadoDao.deleteAllEmpleados()
    }
}
