package com.registro.empleados.data.local.dao

import androidx.room.*
import com.registro.empleados.data.local.entity.HorasEmpleadoMesEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de horas trabajadas por empleado por mes.
 */
@Dao
interface HorasEmpleadoMesDao {
    
    @Query("SELECT * FROM horas_empleado_mes WHERE legajoEmpleado = :legajo ORDER BY año DESC, mes DESC")
    fun getHorasByLegajo(legajo: String): Flow<List<HorasEmpleadoMesEntity>>
    
    @Query("SELECT * FROM horas_empleado_mes WHERE legajoEmpleado = :legajo AND año = :año AND mes = :mes")
    suspend fun getHorasByLegajoAndMes(legajo: String, año: Int, mes: Int): HorasEmpleadoMesEntity?
    
    @Query("SELECT * FROM horas_empleado_mes WHERE año = :año AND mes = :mes ORDER BY totalHoras DESC")
    fun getHorasByMes(año: Int, mes: Int): Flow<List<HorasEmpleadoMesEntity>>
    
    @Query("SELECT * FROM horas_empleado_mes WHERE año = :año ORDER BY mes, totalHoras DESC")
    fun getHorasByAño(año: Int): Flow<List<HorasEmpleadoMesEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHorasEmpleadoMes(horas: HorasEmpleadoMesEntity)
    
    @Update
    suspend fun updateHorasEmpleadoMes(horas: HorasEmpleadoMesEntity)
    
    @Delete
    suspend fun deleteHorasEmpleadoMes(horas: HorasEmpleadoMesEntity)
    
    @Query("DELETE FROM horas_empleado_mes WHERE legajoEmpleado = :legajo AND año = :año AND mes = :mes")
    suspend fun deleteHorasByLegajoAndMes(legajo: String, año: Int, mes: Int)
}
