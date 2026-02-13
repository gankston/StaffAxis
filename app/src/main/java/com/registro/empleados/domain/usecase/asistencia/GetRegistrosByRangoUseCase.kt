package com.registro.empleados.domain.usecase.asistencia

import com.registro.empleados.domain.model.RegistroAsistencia
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Caso de uso para obtener registros de asistencia en un rango de fechas.
 * Encapsula la lógica de negocio para consultas de registros.
 */
class GetRegistrosByRangoUseCase @Inject constructor(
    private val registroRepository: RegistroAsistenciaRepository
) {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    /**
     * Ejecuta el caso de uso para obtener registros en un rango de fechas.
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @param legajo Legajo específico (opcional, si se proporciona filtra por empleado)
     * @return Lista de registros de asistencia
     */
    suspend operator fun invoke(
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        legajo: String? = null
    ): List<RegistroAsistencia> {
        validateInputs(fechaInicio, fechaFin)
        
        val fechaInicioStr = fechaInicio.format(dateFormatter)
        val fechaFinStr = fechaFin.format(dateFormatter)
        
        return if (legajo != null && legajo.isNotBlank()) {
            registroRepository.getRegistrosByLegajoYRango(legajo, fechaInicioStr, fechaFinStr)
        } else {
            registroRepository.getRegistrosByRango(fechaInicioStr, fechaFinStr)
        }
    }
    
    /**
     * Valida los datos de entrada.
     */
    private fun validateInputs(fechaInicio: LocalDate, fechaFin: LocalDate) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin")
        }
        
        val diasDiferencia = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin)
        if (diasDiferencia > 365) {
            throw IllegalArgumentException("El rango de fechas no puede ser mayor a un año")
        }
    }
}
