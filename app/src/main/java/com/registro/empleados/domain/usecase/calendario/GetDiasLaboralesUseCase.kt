package com.registro.empleados.domain.usecase.calendario

import com.registro.empleados.domain.model.DiaLaboral
import com.registro.empleados.domain.repository.DiaLaboralRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para obtener información de días laborales en un rango de fechas.
 * Encapsula la lógica de negocio para consultar días laborales.
 */
class GetDiasLaboralesUseCase @Inject constructor(
    private val diaLaboralRepository: DiaLaboralRepository
) {
    
    /**
     * Ejecuta el caso de uso para obtener días laborales en un rango.
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de días laborales
     */
    suspend operator fun invoke(
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): List<DiaLaboral> {
        validateInputs(fechaInicio, fechaFin)
        
        return diaLaboralRepository.getDiasByRango(fechaInicio, fechaFin)
    }
    
    /**
     * Ejecuta el caso de uso para obtener información de un día específico.
     * @param fecha Fecha a consultar
     * @return Información del día o null si no está registrado
     */
    suspend operator fun invoke(fecha: LocalDate): DiaLaboral? {
        return diaLaboralRepository.getDiaByFecha(fecha)
    }
    
    /**
     * Valida los datos de entrada.
     */
    private fun validateInputs(fechaInicio: LocalDate, fechaFin: LocalDate) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin")
        }
    }
}
