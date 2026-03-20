package com.registro.empleados.domain.model

import java.time.LocalDate

/**
 * Modelo de dominio para un empleado.
 * Representa la lógica de negocio de un empleado sin dependencias de frameworks.
 */
data class Empleado(
    val id: Long = 0,
    /** ID del empleado en el backend (API). Obligatorio para cargar horas y enviar cierre de tarja. */
    val employeeIdBackend: String? = null,
    val legajo: String?,
    val nombreCompleto: String,
    val sector: String,
    val fechaIngreso: LocalDate,
    val activo: Boolean = true,
    val fechaCreacion: LocalDate = LocalDate.now(),
    val observacion: String? = null,
    val observaciones: String? = null,
    val dni: String? = null
) {
    /**
     * Nombre del empleado (primera palabra del nombreCompleto).
     */
    val nombre: String
        get() = nombreCompleto.split(" ").firstOrNull() ?: ""
    
    /**
     * Apellido del empleado (última palabra del nombreCompleto).
     */
    val apellido: String
        get() = nombreCompleto.split(" ").lastOrNull() ?: ""

    /**
     * Verifica si el empleado está activo.
     */
    fun estaActivo(): Boolean = activo

    /**
     * Calcula los años de antigüedad del empleado.
     */
    fun calcularAntiguedad(): Int {
        val hoy = LocalDate.now()
        return hoy.year - fechaIngreso.year
    }
}
