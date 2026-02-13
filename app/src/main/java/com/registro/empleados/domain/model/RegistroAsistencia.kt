package com.registro.empleados.domain.model

/**
 * Modelo de dominio para un registro de asistencia.
 * Nueva estructura: solo horas trabajadas manuales (4, 8 o 12 horas).
 */
data class RegistroAsistencia(
    val id: Long = 0,
    val legajoEmpleado: String,  // FK a Empleado usando legajo
    val fecha: String, // Formato "yyyy-MM-dd"
    val horasTrabajadas: Int, // Solo 4, 8 o 12
    val observaciones: String? = null,
    val fechaRegistro: Long = System.currentTimeMillis() // Timestamp de cuándo se cargó
) {
    
    /**
     * Verifica si el registro tiene observaciones.
     */
    fun tieneObservaciones(): Boolean = !observaciones.isNullOrBlank()

    /**
     * Obtiene las horas trabajadas en formato legible.
     */
    fun obtenerHorasTrabajadasFormateadas(): String {
        return "${horasTrabajadas}h"
    }

    /**
     * Obtiene el estado del registro como string.
     */
    fun obtenerEstado(): String = "Registrado"

    /**
     * Verifica si las horas son válidas (4, 8 o 12).
     */
    fun tieneHorasValidas(): Boolean = horasTrabajadas in listOf(4, 8, 12)
}
