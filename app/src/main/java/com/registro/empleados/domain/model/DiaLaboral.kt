package com.registro.empleados.domain.model

import java.time.LocalDate

/**
 * Modelo de dominio para un día laboral.
 * Representa la información sobre si un día es laboral, feriado o fin de semana.
 */
data class DiaLaboral(
    val fecha: LocalDate,
    val esLaboral: Boolean,
    val tipoDia: TipoDia,
    val descripcion: String? = null,
    val fechaActualizacion: LocalDate = LocalDate.now()
) {
    
    /**
     * Enum que define los tipos de días.
     */
    enum class TipoDia {
        LABORAL,
        FERIADO,
        FIN_DE_SEMANA
    }

    /**
     * Verifica si es un día laboral normal.
     */
    fun esDiaLaboralNormal(): Boolean = esLaboral && tipoDia == TipoDia.LABORAL

    /**
     * Verifica si es un feriado.
     */
    fun esFeriado(): Boolean = tipoDia == TipoDia.FERIADO

    /**
     * Verifica si es fin de semana.
     */
    fun esFinDeSemana(): Boolean = tipoDia == TipoDia.FIN_DE_SEMANA

    /**
     * Obtiene la descripción del día o un valor por defecto.
     */
    fun obtenerDescripcion(): String = descripcion ?: when (tipoDia) {
        TipoDia.LABORAL -> "Día laboral"
        TipoDia.FERIADO -> "Feriado"
        TipoDia.FIN_DE_SEMANA -> "Fin de semana"
    }

    /**
     * Obtiene el color asociado al tipo de día (para UI).
     */
    fun obtenerColorTipoDia(): String = when (tipoDia) {
        TipoDia.LABORAL -> "#4CAF50" // Verde
        TipoDia.FERIADO -> "#F44336" // Rojo
        TipoDia.FIN_DE_SEMANA -> "#9E9E9E" // Gris
    }
}
