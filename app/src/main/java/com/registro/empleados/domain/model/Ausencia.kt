package com.registro.empleados.domain.model

import java.time.LocalDate

data class Ausencia(
    val id: Long = 0,
    val legajoEmpleado: String, // Este es el employee_id del backend
    val nombreEmpleado: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val motivo: String? = null,
    val observaciones: String? = null,
    val esJustificada: Boolean = false,
    val fechaCreacion: LocalDate = LocalDate.now(),
    val syncStatus: String = "pending" // 'pending', 'sent', 'failed'
) {
    // Verificar si una fecha está dentro del rango de ausencia
    fun incluyeFecha(fecha: LocalDate): Boolean {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin)
    }
    
    // Obtener lista de todas las fechas en el rango
    fun getFechasAfectadas(): List<LocalDate> {
        val fechas = mutableListOf<LocalDate>()
        var fechaActual = fechaInicio
        while (!fechaActual.isAfter(fechaFin)) {
            fechas.add(fechaActual)
            fechaActual = fechaActual.plusDays(1)
        }
        return fechas
    }
}
