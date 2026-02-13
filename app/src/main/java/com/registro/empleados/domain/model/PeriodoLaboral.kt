package com.registro.empleados.domain.model

import java.time.LocalDate

/**
 * Modelo de dominio para un período laboral personalizado.
 * Cada período va del día 26 de un mes al día 25 del mes siguiente.
 */
data class PeriodoLaboral(
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate
) {
    
    /**
     * Nombre del período en formato legible.
     * Ejemplo: "Octubre 2025" para el período del 26/09/2025 al 25/10/2025
     */
    val nombre: String
        get() {
            val mesFin = fechaFin.month
            val añoFin = fechaFin.year
            return "${mesFin.name.lowercase().replaceFirstChar { it.uppercase() }} $añoFin"
        }

    /**
     * Verifica si una fecha específica está dentro de este período.
     */
    fun contieneFecha(fecha: LocalDate): Boolean {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin)
    }

    /**
     * Obtiene el período anterior.
     */
    fun obtenerPeriodoAnterior(): PeriodoLaboral {
        val nuevaFechaFin = fechaInicio.minusDays(1)
        val nuevaFechaInicio = nuevaFechaFin.withDayOfMonth(26).minusMonths(1)
        return PeriodoLaboral(nuevaFechaInicio, nuevaFechaFin)
    }

    /**
     * Obtiene el período siguiente.
     */
    fun obtenerPeriodoSiguiente(): PeriodoLaboral {
        val nuevaFechaInicio = fechaFin.plusDays(1)
        val nuevaFechaFin = nuevaFechaInicio.withDayOfMonth(25).plusMonths(1)
        return PeriodoLaboral(nuevaFechaInicio, nuevaFechaFin)
    }

    companion object {
        /**
         * Calcula el período laboral actual basado en la fecha de hoy.
         * 
         * Lógica:
         * - Si hoy es día 26 o posterior: período actual es del 26 de este mes al 25 del próximo
         * - Si hoy es antes del día 26: período actual es del 26 del mes pasado al 25 de este mes
         */
        fun calcularPeriodoActual(): PeriodoLaboral {
            val hoy = LocalDate.now()
            val diaDelMes = hoy.dayOfMonth

            return if (diaDelMes >= 26) {
                // Estamos en el período actual: 26 de este mes al 25 del próximo
                val inicio = hoy.withDayOfMonth(26)
                val fin = hoy.plusMonths(1).withDayOfMonth(25)
                PeriodoLaboral(inicio, fin)
            } else {
                // Estamos en el período anterior: 26 del mes pasado al 25 de este mes
                val inicio = hoy.minusMonths(1).withDayOfMonth(26)
                val fin = hoy.withDayOfMonth(25)
                PeriodoLaboral(inicio, fin)
            }
        }

        /**
         * Calcula un período específico para un mes y año dados.
         */
        fun calcularPeriodoParaMes(mes: Int, año: Int): PeriodoLaboral {
            val fechaBase = LocalDate.of(año, mes, 26)
            val fechaFin = fechaBase.withDayOfMonth(25).plusMonths(1)
            return PeriodoLaboral(fechaBase, fechaFin)
        }
    }
}
