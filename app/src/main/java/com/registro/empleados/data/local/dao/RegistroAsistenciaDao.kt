package com.registro.empleados.data.local.dao

import androidx.room.*
import com.registro.empleados.data.local.entity.RegistroAsistenciaEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * DAO para operaciones de registros de asistencia.
 * Actualizado para nueva estructura: solo horas trabajadas manuales (4, 8 o 12 horas).
 */
@Dao
interface RegistroAsistenciaDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistro(registro: RegistroAsistenciaEntity): Long
    
    @Update
    suspend fun updateRegistro(registro: RegistroAsistenciaEntity)
    
    @Query("SELECT * FROM registros_asistencia WHERE legajo_empleado = :legajo AND fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    suspend fun getRegistrosByLegajoYRango(
        legajo: String, 
        fechaInicio: String, 
        fechaFin: String
    ): List<RegistroAsistenciaEntity>
    
    @Query("SELECT * FROM registros_asistencia WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC, legajo_empleado ASC")
    suspend fun getRegistrosByRango(
        fechaInicio: String, 
        fechaFin: String
    ): List<RegistroAsistenciaEntity>
    
    // NUEVA QUERY: Verificar si ya existe registro para empleado en fecha específica
    @Query("SELECT * FROM registros_asistencia WHERE legajo_empleado = :legajo AND fecha = :fecha")
    suspend fun getRegistroByLegajoYFecha(legajo: String, fecha: String): RegistroAsistenciaEntity?
    
    @Query("DELETE FROM registros_asistencia WHERE id = :id")
    suspend fun deleteRegistro(id: Long)

    @Query("DELETE FROM registros_asistencia WHERE legajo_empleado = :legajo AND fecha = :fecha")
    suspend fun deleteRegistroByLegajoYFecha(legajo: String, fecha: String)
    
    // NUEVA QUERY: Sumar total de horas en un período
    @Query("SELECT SUM(horas_trabajadas) FROM registros_asistencia WHERE legajo_empleado = :legajo AND fecha BETWEEN :fechaInicio AND :fechaFin")
    suspend fun getTotalHorasByLegajoYPeriodo(legajo: String, fechaInicio: String, fechaFin: String): Int?
    
    // Queries adicionales para compatibilidad
    @Query("SELECT * FROM registros_asistencia WHERE legajo_empleado = :legajo ORDER BY fecha DESC")
    fun getRegistrosByLegajo(legajo: String): Flow<List<RegistroAsistenciaEntity>>
    
    @Query("SELECT * FROM registros_asistencia WHERE fecha = :fecha ORDER BY legajo_empleado ASC")
    fun getRegistrosByFecha(fecha: String): Flow<List<RegistroAsistenciaEntity>>
    
    @Query("SELECT * FROM registros_asistencia ORDER BY fecha DESC, legajo_empleado ASC")
    fun getAllRegistros(): Flow<List<RegistroAsistenciaEntity>>

    @Query("DELETE FROM registros_asistencia")
    suspend fun deleteAllRegistros()

    @Query("SELECT * FROM registros_asistencia WHERE legajo_empleado = :legajo AND fecha = :fecha")
    suspend fun getRegistrosByLegajoAndFecha(
        legajo: String,
        fecha: LocalDate
    ): List<RegistroAsistenciaEntity>
}
