package com.registro.empleados.domain.usecase.horas

import com.registro.empleados.domain.model.HorasEmpleadoMes
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener las horas trabajadas mensuales.
 */
class GetHorasMensualesUseCase @Inject constructor(
    private val horasRepository: HorasEmpleadoMesRepository
) {
    
    /**
     * Obtiene todas las horas trabajadas de un empleado.
     */
    suspend operator fun invoke(legajo: String): Flow<List<HorasEmpleadoMes>> {
        return horasRepository.getHorasByLegajo(legajo)
    }
    
    /**
     * Obtiene las horas trabajadas de un empleado en un mes específico.
     */
    suspend fun getHorasByMes(legajo: String, año: Int, mes: Int): HorasEmpleadoMes? {
        return horasRepository.getHorasByLegajoAndMes(legajo, año, mes)
    }
    
    /**
     * Obtiene las horas trabajadas de todos los empleados en un mes específico.
     */
    suspend fun getHorasByMes(año: Int, mes: Int): Flow<List<HorasEmpleadoMes>> {
        return horasRepository.getHorasByMes(año, mes)
    }
    
    /**
     * Obtiene las horas trabajadas de todos los empleados en un año específico.
     */
    suspend fun getHorasByAño(año: Int): Flow<List<HorasEmpleadoMes>> {
        return horasRepository.getHorasByAño(año)
    }
}
