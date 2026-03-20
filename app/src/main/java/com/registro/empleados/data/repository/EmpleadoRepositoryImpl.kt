package com.registro.empleados.data.repository

import com.registro.empleados.domain.exception.TransferConflictException
import org.json.JSONObject
import com.registro.empleados.data.local.dao.EmpleadoDao
import com.registro.empleados.data.local.database.AppDatabase
import com.registro.empleados.data.local.entity.EmpleadoEntity
import com.registro.empleados.data.mapper.EmpleadoMapper
import com.registro.empleados.data.remote.api.EmployeesApiService
import com.registro.empleados.data.remote.dto.CreateEmployeeRequestDto
import com.registro.empleados.data.remote.dto.UpdateEmployeeRequestDto
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.repository.EmpleadoRepository
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de empleados.
 * Maneja la lógica de acceso a datos y conversión entre capas.
 */
@Singleton
class EmpleadoRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val employeesApiService: EmployeesApiService
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
        android.util.Log.d("EmpleadoRepositoryImpl", "Empleado recibido - ID: ${empleado.id}, Legajo: ${empleado.legajo}, Nombre: ${empleado.nombreCompleto}, BackendID: ${empleado.employeeIdBackend}")
        
        // 1. Update local database
        val entity = EmpleadoMapper.toEntity(empleado)
        empleadoDao.updateEmpleado(entity)
        android.util.Log.d("EmpleadoRepositoryImpl", "✅ Local DAO update ejecutado")
        
        // 2. Update server if backendId exists
        val backendId = empleado.employeeIdBackend
        if (!backendId.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val sectorId = database.sectorDao().getSectorIdByName(empleado.sector) ?: ""
                    
                    val parts = empleado.nombreCompleto.trim().split(" ")
                    val firstName = parts.firstOrNull() ?: ""
                    val lastName = if (parts.size > 1) parts.drop(1).joinToString(" ") else ""

                    val request = UpdateEmployeeRequestDto(
                        firstName = firstName,
                        lastName = lastName,
                        dni = empleado.dni ?: (if (empleado.legajo?.startsWith("AUTO_") == true) null else empleado.legajo),
                        externalCode = empleado.legajo,
                        sectorId = sectorId.ifBlank { null }
                    )
                    
                    android.util.Log.d("EmpleadoRepositoryImpl", "Enviando PUT a API para $backendId. body: $request")
                    val response = employeesApiService.updateEmployee(backendId, request)
                    
                    if (response.isSuccessful) {
                        android.util.Log.i("EmpleadoRepositoryImpl", "✅ Update API éxito para $backendId")
                        // Podríamos actualizar localmente con la respuesta del API si fuera necesario
                    } else {
                        val errorStr = response.errorBody()?.string() ?: "sin cuerpo de error"
                        android.util.Log.e("EmpleadoRepositoryImpl", "❌ Error API (${response.code()}): $errorStr")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("EmpleadoRepositoryImpl", "❌ Excepción en Update API", e)
                }
            }
        } else {
            android.util.Log.w("EmpleadoRepositoryImpl", "⚠️ No se envió al servidor (employeeIdBackend es null)")
        }
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

    override suspend fun clearLocalTablesForSync() {
        withContext(Dispatchers.IO) {
            // Forzar sync "limpio" SOLO de sectores + empleados (no borrar registros/outbox/etc.)
            database.sectorDao().deleteAll()
            database.empleadoDao().deleteAllEmpleados()
        }
    }


    override suspend fun createEmployeeViaApi(
        firstName: String,
        lastName: String,
        documentNumber: String?,
        sectorId: String,
        sectorName: String,
        forceTransfer: Boolean
    ): Result<Empleado> = withContext(Dispatchers.IO) {
        try {
            val dniEnvio = documentNumber?.trim() ?: ""
            val request = CreateEmployeeRequestDto(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                dni = dniEnvio,
                sectorId = sectorId,
                forceTransfer = forceTransfer
            )
            android.util.Log.d(
                "CreateEmployee",
                "POST body: first_name=${request.firstName}, last_name=${request.lastName}, dni=\"${request.dni}\", sector_id=${request.sectorId}, force_transfer=${request.forceTransfer}"
            )
            val response = employeesApiService.createEmployee(request)
            android.util.Log.d("CreateEmployee", "API Response Code: ${response.code()}")

            if (!response.isSuccessful) {
                if (response.code() == 409) {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.w("CreateEmployee", "CONFLICT (409) detected: $errorBody")
                    if (errorBody != null) {
                        try {
                            val json = JSONObject(errorBody)
                            val empObj = json.getJSONObject("employee")
                            val existingSectorName = empObj.optString("sector_name", "otro sector")
                            val existingEmployee = Empleado(
                                id = 0,
                                employeeIdBackend = empObj.getString("id"),
                                legajo = empObj.optString("dni", ""),
                                nombreCompleto = "${empObj.optString("last_name", "")} ${empObj.optString("first_name", "")}".trim().uppercase(),
                                sector = existingSectorName,
                                fechaIngreso = LocalDate.now(),
                                activo = true,
                                dni = empObj.optString("dni", null)
                            )
                            return@withContext Result.failure(TransferConflictException(
                                "El empleado ya existe en $existingSectorName",
                                existingEmployee,
                                existingSectorName
                            ))
                        } catch (e: Exception) {
                            android.util.Log.e("CreateEmployee", "Error al parsear 409: ${e.message}")
                        }
                    }
                }
                val errorBody = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                val msg = errorBody ?: "Error HTTP ${response.code()}"
                return@withContext Result.failure(Exception(msg))
            }

            val dto = response.body()
            if (dto == null) {
                return@withContext Result.failure(Exception("La API no devolvió datos"))
            }

            val backendId = dto.id.trim().takeIf { it.isNotBlank() }
                ?: return@withContext Result.failure(Exception("La API no devolvió el ID del empleado"))
            android.util.Log.d("CreateEmployee", "API OK: id=$backendId, dni=${dto.dni}")
            val legajo = dto.externalCode ?: dto.dni?.takeIf { it.isNotBlank() } ?: dniEnvio?.takeIf { it.isNotBlank() }
            val dniLocal = dto.dni ?: dniEnvio?.takeIf { it.isNotBlank() }
            val nombreCompleto = "${dto.lastName} ${dto.firstName}".trim().uppercase()
            val empleado = Empleado(
                id = 0,
                employeeIdBackend = backendId,
                legajo = legajo,
                nombreCompleto = nombreCompleto,
                sector = sectorName,
                fechaIngreso = LocalDate.now(),
                activo = dto.isActive,
                dni = dniLocal
            )
            val entity = EmpleadoMapper.toEntity(empleado)
            empleadoDao.insertEmpleado(entity)
            Result.success(empleado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
