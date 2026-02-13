package com.registro.empleados.data.database.daos

import androidx.room.*

/**
 * Data Access Object para operaciones CRUD de días laborales.
 * Maneja la información sobre feriados, fines de semana y días laborales.
 */
@Dao
interface DiaLaboralDao {
    
    /**
     * Inserta múltiples días laborales en la base de datos.
     * Si ya existe un día con la misma fecha, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDias(dias: List<com.registro.empleados.data.database.entities.DiaLaboral>)

    /**
     * Obtiene días laborales en un rango de fechas específico.
     * Los días se ordenan por fecha ascendente.
     */
    @Query("""
        SELECT * FROM dias_laborales 
        WHERE fecha BETWEEN :fechaInicio AND :fechaFin 
        ORDER BY fecha ASC
    """)
    suspend fun getDiasByRango(
        fechaInicio: String, 
        fechaFin: String
    ): List<com.registro.empleados.data.database.entities.DiaLaboral>

    /**
     * Obtiene información de un día específico por su fecha.
     */
    @Query("SELECT * FROM dias_laborales WHERE fecha = :fecha")
    suspend fun getDiaByFecha(fecha: String): com.registro.empleados.data.database.entities.DiaLaboral?

    /**
     * Cuenta la cantidad de días laborales en un rango de fechas.
     * Útil para cálculos de salarios y reportes.
     */
    @Query("""
        SELECT COUNT(*) FROM dias_laborales 
        WHERE fecha BETWEEN :fechaInicio AND :fechaFin 
        AND es_laboral = 1
    """)
    suspend fun contarDiasLaborales(fechaInicio: String, fechaFin: String): Int

    /**
     * Elimina días laborales antiguos para mantener la base de datos optimizada.
     * Se recomienda mantener solo los últimos 2 años de datos.
     */
    @Query("DELETE FROM dias_laborales WHERE fecha < :fechaLimite")
    suspend fun eliminarDiasAntiguos(fechaLimite: String)

    /**
     * Verifica si un día específico es laboral.
     */
    @Query("SELECT es_laboral FROM dias_laborales WHERE fecha = :fecha")
    suspend fun esDiaLaboral(fecha: String): Boolean?
}
