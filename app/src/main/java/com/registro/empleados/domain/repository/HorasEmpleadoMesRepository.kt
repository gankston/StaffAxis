package com.registro.empleados.domain.repository

import com.registro.empleados.domain.model.HorasEmpleadoMes
import kotlinx.coroutines.flow.Flow

/**
 * Interface del repositorio para horas trabajadas por empleado por mes.
 */
interface HorasEmpleadoMesRepository {
    
    /**
     * Obtiene todas las horas trabajadas de un empleado.
     */
    suspend fun getHorasByLegajo(legajo: String): Flow<List<HorasEmpleadoMes>>
    
    /**
     * Obtiene las horas trabajadas de un empleado en un mes específico.
     */
    suspend fun getHorasByLegajoAndMes(legajo: String, año: Int, mes: Int): HorasEmpleadoMes?
    
    /**
     * Obtiene las horas trabajadas de todos los empleados en un mes específico.
     */
    suspend fun getHorasByMes(año: Int, mes: Int): Flow<List<HorasEmpleadoMes>>
    
    /**
     * Obtiene las horas trabajadas de todos los empleados en un año específico.
     */
    suspend fun getHorasByAño(año: Int): Flow<List<HorasEmpleadoMes>>
    
    /**
     * Inserta o actualiza las horas trabajadas de un empleado en un mes.
     */
    suspend fun insertOrUpdateHoras(horas: HorasEmpleadoMes)
    
    /**
     * Elimina las horas trabajadas de un empleado en un mes específico.
     */
    suspend fun deleteHorasByLegajoAndMes(legajo: String, año: Int, mes: Int)
}
