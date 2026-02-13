package com.registro.empleados.domain.model

import java.time.LocalDate

/**
 * Modelo para almacenar las horas trabajadas por empleado en un mes específico.
 */
data class HorasEmpleadoMes(
    val legajoEmpleado: String,
    val año: Int,
    val mes: Int,
    val totalHoras: Double,
    val diasTrabajados: Int,
    val promedioDiario: Double,
    val ultimaActualizacion: LocalDate = LocalDate.now()
) {
    /**
     * Nombre del mes en español.
     */
    val nombreMes: String
        get() = when (mes) {
            1 -> "Enero"
            2 -> "Febrero"
            3 -> "Marzo"
            4 -> "Abril"
            5 -> "Mayo"
            6 -> "Junio"
            7 -> "Julio"
            8 -> "Agosto"
            9 -> "Septiembre"
            10 -> "Octubre"
            11 -> "Noviembre"
            12 -> "Diciembre"
            else -> "Desconocido"
        }
    
    /**
     * Período en formato legible.
     */
    val periodo: String
        get() = "$nombreMes $año"
}
