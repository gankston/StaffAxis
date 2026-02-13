package com.registro.empleados.domain.usecase.calendario

import com.registro.empleados.domain.model.DiaLaboral
import com.registro.empleados.domain.model.PeriodoLaboral
import com.registro.empleados.domain.model.RegistroAsistencia
import com.registro.empleados.domain.repository.DiaLaboralRepository
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Caso de uso para obtener datos completos del calendario de un período específico.
 * Combina información de días laborales y registros de asistencia.
 */
class GetCalendarioPeriodoUseCase @Inject constructor(
    private val diaLaboralRepository: DiaLaboralRepository,
    private val registroAsistenciaRepository: RegistroAsistenciaRepository
) {
    
    /**
     * Datos completos del calendario para un período.
     */
    data class CalendarioPeriodoData(
        val periodo: PeriodoLaboral,
        val diasLaborales: List<DiaLaboral>,
        val registrosAsistencia: List<RegistroAsistencia>
    )
    
    /**
     * Ejecuta el caso de uso para obtener datos del calendario.
     * @param periodo Período laboral a consultar
     * @return Datos completos del calendario
     */
    suspend operator fun invoke(periodo: PeriodoLaboral): CalendarioPeriodoData {
        // Obtener días laborales del período
        val diasLaborales = diaLaboralRepository.getDiasByRango(
            periodo.fechaInicio,
            periodo.fechaFin
        )
        
        // Obtener registros de asistencia del período
        val registrosAsistencia = registroAsistenciaRepository.getRegistrosByRango(
            periodo.fechaInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            periodo.fechaFin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )
        
        return CalendarioPeriodoData(
            periodo = periodo,
            diasLaborales = diasLaborales,
            registrosAsistencia = registrosAsistencia
        )
    }
}
