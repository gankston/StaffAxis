package com.registro.empleados.data.local.dao

import androidx.room.*
import com.registro.empleados.data.local.entity.EmpleadoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de empleados.
 */
@Dao
interface EmpleadoDao {
    
    @Query("SELECT * FROM empleados WHERE activo = 1 ORDER BY nombreCompleto")
    fun getAllEmpleadosActivos(): Flow<List<EmpleadoEntity>>
    
    @Query("SELECT * FROM empleados ORDER BY nombreCompleto")
    fun getAllEmpleados(): Flow<List<EmpleadoEntity>>
    
    @Query("SELECT * FROM empleados WHERE legajo = :legajo AND activo = 1")
    suspend fun getEmpleadoByLegajo(legajo: String): EmpleadoEntity?

    @Query("SELECT * FROM empleados WHERE id = :id")
    suspend fun getEmpleadoById(id: Long): EmpleadoEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmpleado(empleado: EmpleadoEntity)
    
    @Update
    suspend fun updateEmpleado(empleado: EmpleadoEntity)
    
    @Delete
    suspend fun deleteEmpleado(empleado: EmpleadoEntity)
    
    @Query("UPDATE empleados SET activo = :activo WHERE id = :id")
    suspend fun updateEstadoEmpleado(id: Long, activo: Boolean)
    
    @Query("SELECT * FROM empleados WHERE sector = :sector AND activo = 1 ORDER BY nombreCompleto")
    fun getEmpleadosBySector(sector: String): Flow<List<EmpleadoEntity>>
    
    @Query("SELECT DISTINCT sector FROM empleados WHERE activo = 1 ORDER BY sector")
    fun getSectores(): Flow<List<String>>
    
    @Query("UPDATE empleados SET activo = 0 WHERE legajo = :legajo")
    suspend fun darDeBajaEmpleado(legajo: String)
    
    @Query("UPDATE empleados SET legajo = NULL WHERE id = :id")
    suspend fun quitarLegajoEmpleado(id: Long)
    
    @Query("SELECT * FROM empleados WHERE nombreCompleto LIKE '%' || :nombre || '%' AND activo = 1 ORDER BY nombreCompleto")
    fun buscarEmpleadosPorNombre(nombre: String): Flow<List<EmpleadoEntity>>
    
    @Query("SELECT * FROM empleados WHERE nombreCompleto LIKE '%' || :apellido || '%' AND activo = 1 ORDER BY nombreCompleto")
    fun buscarEmpleadosPorApellido(apellido: String): Flow<List<EmpleadoEntity>>
    
    @Query("SELECT * FROM empleados WHERE nombreCompleto LIKE '%' || :nombre || '%' AND activo = 1 ORDER BY nombreCompleto")
    fun buscarEmpleadosPorNombreYApellido(nombre: String): Flow<List<EmpleadoEntity>>
    
    // NUEVA QUERY: Búsqueda combinada por legajo o nombre completo
    @Query("""
        SELECT * FROM empleados 
        WHERE activo = 1 
        AND (
            legajo LIKE :query 
            OR LOWER(nombreCompleto) LIKE '%' || LOWER(:query) || '%'
        )
        ORDER BY nombreCompleto ASC
    """)
    fun buscarEmpleados(query: String): Flow<List<EmpleadoEntity>>

    @Query("DELETE FROM empleados")
    suspend fun deleteAllEmpleados()
    
    @Query("DELETE FROM empleados WHERE id = :id")
    suspend fun deleteById(id: Long)

    // Mantener compatibilidad con borrado por legajo si existe
    @Query("DELETE FROM empleados WHERE legajo = :legajo")
    suspend fun deleteByLegajo(legajo: String)
    
    @Query("UPDATE empleados SET legajo = NULL WHERE legajo LIKE 'AUTO_%'")
    suspend fun limpiarLegajosAutomaticos()
    
    @Query("UPDATE empleados SET nombreCompleto = :nuevoNombre, legajo = :nuevoLegajo WHERE sector = :sector AND nombreCompleto LIKE '%' || :apellidoBusqueda || '%' AND nombreCompleto LIKE '%' || :nombreBusqueda || '%'")
    suspend fun corregirEmpleadoPorNombre(sector: String, apellidoBusqueda: String, nombreBusqueda: String, nuevoNombre: String, nuevoLegajo: String?)
}
