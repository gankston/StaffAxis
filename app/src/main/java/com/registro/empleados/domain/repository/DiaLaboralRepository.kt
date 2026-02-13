package com.registro.empleados.domain.repository

import com.registro.empleados.domain.model.DiaLaboral
import java.time.LocalDate

/**
 * Interface del repositorio para operaciones con días laborales.
 * Define el contrato que debe implementar la capa de datos.
 */
interface DiaLaboralRepository {
    
    /**
     * Obtiene información de días laborales en un rango de fechas.
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de días laborales ordenados por fecha
     */
    suspend fun getDiasByRango(
        fechaInicio: LocalDate, 
        fechaFin: LocalDate
    ): List<DiaLaboral>

    /**
     * Obtiene información de un día específico.
     * @param fecha Fecha a consultar
     * @return Información del día o null si no está registrado
     */
    suspend fun getDiaByFecha(fecha: LocalDate): DiaLaboral?

    /**
     * Verifica si un día específico es laboral.
     * @param fecha Fecha a verificar
     * @return true si es laboral, false si no es laboral, null si no está registrado
     */
    suspend fun esDiaLaboral(fecha: LocalDate): Boolean?

    /**
     * Inserta o actualiza múltiples días laborales.
     * @param dias Lista de días laborales a insertar/actualizar
     */
    suspend fun insertDias(dias: List<DiaLaboral>)

    /**
     * Cuenta la cantidad de días laborales en un rango de fechas.
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Cantidad de días laborales
     */
    suspend fun contarDiasLaborales(fechaInicio: LocalDate, fechaFin: LocalDate): Int

    /**
     * Elimina días laborales antiguos para optimizar la base de datos.
     * @param fechaLimite Fecha límite: se eliminan días anteriores a esta fecha
     */
    suspend fun eliminarDiasAntiguos(fechaLimite: LocalDate)

    /**
     * Sincroniza los días laborales con una fuente externa.
     * @param año Año para sincronizar
     */
    suspend fun sincronizarFeriados(año: Int)
}
