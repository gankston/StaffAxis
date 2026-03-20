package com.registro.empleados.domain.usecase.sync

import android.util.Log
import com.registro.empleados.data.local.dao.EmpleadoDao
import com.registro.empleados.data.local.entity.EmpleadoEntity
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.data.remote.api.EmployeesApiService
import com.registro.empleados.data.remote.api.SectorsApiService
import com.registro.empleados.domain.repository.EmpleadoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

/**
 * Sincroniza empleados desde la API de producción.
 * Limpia tablas locales, obtiene sectores, descarga empleados por sector_id e inserta en BD.
 * Se usa al seleccionar un sector (Bienvenida) o al forzar actualización (Dashboard).
 */
class SyncEmpleadosFromApiUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository,
    private val appPreferences: AppPreferences,
    private val sectorsApiService: SectorsApiService,
    private val employeesApiService: EmployeesApiService,
    private val empleadoDao: EmpleadoDao
) {

    sealed class Result {
        data object Success : Result()
        data class Error(val message: String) : Result()
    }

    suspend operator fun invoke(): Result {
        return try {
            empleadoRepository.clearLocalTablesForSync()

            val sectorsResponse = sectorsApiService.getSectors()
            if (!sectorsResponse.isSuccessful) {
                return Result.Error("No se pudieron obtener los sectores")
            }

            val sectorId = appPreferences.getSectorId()
            if (sectorId.isNullOrBlank()) {
                return Result.Error("No se pudo determinar el sector actual")
            }

            val sectorNombre = appPreferences.getSectorSeleccionado().ifBlank { "Sector" }

            Log.d("SYNC_DEBUG", "Cargando empleados para sector: $sectorId")
            val employeesResponse = employeesApiService.getEmployees(sectorId)
            Log.d("SYNC_DEBUG", "Respuesta recibida: ${employeesResponse.body()}")

            if (!employeesResponse.isSuccessful) {
                return Result.Error("No se pudieron obtener los empleados")
            }

            val employeesDto = employeesResponse.body()?.employees.orEmpty()
            withContext(Dispatchers.IO) {
                employeesDto.forEach { dto ->
                    val nombreCompleto = "${dto.lastName} ${dto.firstName}".trim().uppercase()
                    val existing = empleadoDao.getEmpleadoByBackendId(dto.id)
                    val entity = EmpleadoEntity(
                        id = existing?.id ?: 0,
                        employeeIdBackend = dto.id,
                        legajo = dto.externalCode,
                        nombreCompleto = nombreCompleto,
                        sector = sectorNombre,
                        fechaIngreso = existing?.fechaIngreso ?: LocalDate.now(),
                        activo = dto.isActive,
                        dni = dto.dni
                    )
                    empleadoDao.insertEmpleado(entity)
                }
            }
            Result.Success
        } catch (e: Exception) {
            Log.e("SyncEmpleadosFromApi", "Error en sync", e)
            Result.Error(e.message ?: "Error desconocido")
        }
    }
}
