package com.registro.empleados.data.local.dao

import androidx.room.*
import com.registro.empleados.data.local.entity.AusenciaEntity
import java.time.LocalDate

@Dao
interface AusenciaDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAusencia(ausencia: AusenciaEntity): Long
    
    @Update
    suspend fun updateAusencia(ausencia: AusenciaEntity)
    
    @Delete
    suspend fun deleteAusencia(ausencia: AusenciaEntity)
    
    @Query("SELECT * FROM ausencia_table WHERE legajoEmpleado = :legajo")
    suspend fun getAusenciasByLegajo(legajo: String): List<AusenciaEntity>
    
    @Query("""
        SELECT * FROM ausencia_table 
        WHERE fechaInicio <= :fecha AND fechaFin >= :fecha
    """)
    suspend fun getAusenciasByFecha(fecha: LocalDate): List<AusenciaEntity>
    
    @Query("""
        SELECT * FROM ausencia_table 
        WHERE fechaInicio <= :fechaFin AND fechaFin >= :fechaInicio
    """)
    suspend fun getAusenciasByRango(
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): List<AusenciaEntity>
    
    @Query("""
        SELECT * FROM ausencia_table 
        WHERE legajoEmpleado = :legajo 
        AND fechaInicio <= :fecha 
        AND fechaFin >= :fecha
    """)
    suspend fun getAusenciaByLegajoYFecha(
        legajo: String,
        fecha: LocalDate
    ): AusenciaEntity?
    
    @Query("SELECT * FROM ausencia_table ORDER BY fechaInicio DESC")
    suspend fun getAllAusencias(): List<AusenciaEntity>

    @Query("SELECT * FROM ausencia_table WHERE syncStatus = 'pending' OR syncStatus = 'failed' LIMIT :limit")
    suspend fun getPendingAusencias(limit: Int): List<AusenciaEntity>

    @Query("UPDATE ausencia_table SET syncStatus = 'sent', attempts = attempts + 1 WHERE id = :id")
    suspend fun markAsSent(id: Long)

    @Query("UPDATE ausencia_table SET syncStatus = 'failed', attempts = attempts + 1, lastError = :error WHERE id = :id")
    suspend fun markAsFailed(id: Long, error: String)
}
