package com.registro.empleados.domain.model

import java.time.LocalDate

data class Ausencia(
    val id: Long = 0,
    val legajoEmpleado: String,
    val nombreEmpleado: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val motivo: String? = null,
    val fechaCreacion: LocalDate = LocalDate.now()
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
