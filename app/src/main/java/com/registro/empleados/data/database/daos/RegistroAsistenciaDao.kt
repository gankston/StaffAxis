package com.registro.empleados.data.database.daos

import androidx.room.*
import com.registro.empleados.data.database.entities.RegistroAsistencia

/**
 * Data Access Object para operaciones CRUD de registros de asistencia.
 * Maneja las relaciones con empleados y cálculos de horas trabajadas.
 */
@Dao
interface RegistroAsistenciaDao {
    
    /**
     * Inserta un nuevo registro de asistencia.
     * Si ya existe un registro para el mismo empleado y fecha, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistro(registro: RegistroAsistencia): Long

    /**
     * Actualiza un registro de asistencia existente.
     */
    @Update
    suspend fun updateRegistro(registro: RegistroAsistencia)

    /**
     * Obtiene registros de asistencia de un empleado específico en un rango de fechas.
     * Los registros se ordenan por fecha descendente (más recientes primero).
     */
    @Query("""
        SELECT * FROM registros_asistencia 
        WHERE legajo_empleado = :legajo 
        AND fecha BETWEEN :fechaInicio AND :fechaFin 
        ORDER BY fecha DESC
    """)
    suspend fun getRegistrosByLegajoYRango(
        legajo: String, 
        fechaInicio: String, 
        fechaFin: String
    ): List<RegistroAsistencia>

    /**
     * Obtiene todos los registros de asistencia en un rango de fechas.
     * Los registros se ordenan por fecha descendente y luego por legajo.
     */
    @Query("""
        SELECT * FROM registros_asistencia 
        WHERE fecha BETWEEN :fechaInicio AND :fechaFin 
        ORDER BY fecha DESC, legajo_empleado ASC
    """)
    suspend fun getRegistrosByRango(
        fechaInicio: String, 
        fechaFin: String
    ): List<RegistroAsistencia>

    /**
     * Obtiene el registro de asistencia de un empleado para una fecha específica.
     */
    @Query("""
        SELECT * FROM registros_asistencia 
        WHERE legajo_empleado = :legajo AND fecha = :fecha
    """)
    suspend fun getRegistroByLegajoYFecha(legajo: String, fecha: String): RegistroAsistencia?

    /**
     * Elimina un registro de asistencia por su ID.
     */
    @Query("DELETE FROM registros_asistencia WHERE id = :id")
    suspend fun deleteRegistro(id: Long)

    /**
     * Obtiene registros de asistencia de un empleado específico para el día actual.
     */
    @Query("""
        SELECT * FROM registros_asistencia 
        WHERE legajo_empleado = :legajo AND fecha = :fecha
    """)
    suspend fun getRegistroHoy(legajo: String, fecha: String): RegistroAsistencia?
}
