package com.registro.empleados.domain.usecase.horas

import com.registro.empleados.domain.model.HorasEmpleadoMes
import com.registro.empleados.domain.repository.EmpleadoRepository
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Caso de uso para actualizar las horas trabajadas mensuales de un empleado.
 */
class ActualizarHorasMensualesUseCase @Inject constructor(
    private val horasRepository: HorasEmpleadoMesRepository,
    private val registroRepository: RegistroAsistenciaRepository,
    private val empleadoRepository: EmpleadoRepository
) {
    
    /**
     * Actualiza las horas trabajadas de un empleado para un mes específico.
     */
    suspend operator fun invoke(legajo: String, año: Int, mes: Int) {
        // Calcular fechas del mes
        val fechaInicio = LocalDate.of(año, mes, 1)
        val fechaFin = fechaInicio.withDayOfMonth(fechaInicio.lengthOfMonth())
        
        // Obtener registros del empleado en ese mes
        val registros = registroRepository.getRegistrosByLegajoYRango(
            legajo, 
            fechaInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 
            fechaFin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )
        
        // Calcular estadísticas
        val diasTrabajados = registros.count { it.horasTrabajadas > 0 }
        val totalHoras = registros.sumOf { it.horasTrabajadas.toDouble() }
        val promedioDiario = if (diasTrabajados > 0) totalHoras / diasTrabajados else 0.0
        
        // Obtener el empleado para conseguir su ID
        val empleado = empleadoRepository.getEmpleadoByLegajo(legajo)
        
        // Crear o actualizar el registro de horas mensuales
        val horasMensuales = HorasEmpleadoMes(
            legajoEmpleado = legajo,
            año = año,
            mes = mes,
            totalHoras = totalHoras,
            diasTrabajados = diasTrabajados,
            promedioDiario = promedioDiario,
            ultimaActualizacion = LocalDate.now()
        )
        
        horasRepository.insertOrUpdateHoras(horasMensuales)
    }
    
    /**
     * Actualiza las horas trabajadas de un empleado para el mes actual.
     */
    suspend fun actualizarMesActual(legajo: String) {
        val hoy = LocalDate.now()
        invoke(legajo, hoy.year, hoy.monthValue)
    }
    
    /**
     * Actualiza las horas trabajadas de todos los empleados para un mes específico.
     */
    suspend fun actualizarMesParaTodosEmpleados(empleados: List<String>, año: Int, mes: Int) {
        empleados.forEach { legajo ->
            invoke(legajo, año, mes)
        }
    }
}
