package com.registro.empleados.data.local.dao

import androidx.room.*
import com.registro.empleados.data.local.entity.DiaLaboralEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * DAO para operaciones de días laborales.
 */
@Dao
interface DiaLaboralDao {
    
    @Query("SELECT * FROM dias_laborales ORDER BY fecha")
    fun getAllDiasLaborales(): Flow<List<DiaLaboralEntity>>
    
    @Query("SELECT * FROM dias_laborales WHERE fecha = :fecha")
    suspend fun getDiaLaboralByFecha(fecha: LocalDate): DiaLaboralEntity?
    
    @Query("SELECT * FROM dias_laborales WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha")
    fun getDiasLaboralesByRango(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<DiaLaboralEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaLaboral(diaLaboral: DiaLaboralEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiasLaborales(diasLaborales: List<DiaLaboralEntity>)
    
    @Update
    suspend fun updateDiaLaboral(diaLaboral: DiaLaboralEntity)
    
    @Delete
    suspend fun deleteDiaLaboral(diaLaboral: DiaLaboralEntity)
    
    @Query("DELETE FROM dias_laborales WHERE fecha BETWEEN :fechaInicio AND :fechaFin")
    suspend fun deleteDiasLaboralesByRango(fechaInicio: LocalDate, fechaFin: LocalDate)
    
    @Query("DELETE FROM dias_laborales")
    suspend fun deleteAllDiasLaborales()
    
    @Query("SELECT COUNT(*) FROM dias_laborales WHERE fecha BETWEEN :fechaInicio AND :fechaFin")
    suspend fun countDiasLaboralesByRango(fechaInicio: LocalDate, fechaFin: LocalDate): Int
}
