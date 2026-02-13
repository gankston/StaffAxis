package com.registro.empleados.data.database.daos

import androidx.room.*
import com.registro.empleados.data.database.entities.Empleado
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para operaciones CRUD de empleados.
 * Utiliza Flow para observación reactiva de cambios en la base de datos.
 */
@Dao
interface EmpleadoDao {
    
    /**
     * Inserta un nuevo empleado en la base de datos.
     * Si ya existe un empleado con el mismo legajo, se aborta la operación.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEmpleado(empleado: Empleado)

    /**
     * Actualiza un empleado existente.
     */
    @Update
    suspend fun updateEmpleado(empleado: Empleado)

    /**
     * Obtiene un empleado específico por su legajo.
     */
    @Query("SELECT * FROM empleados WHERE legajo = :legajo")
    suspend fun getEmpleadoByLegajo(legajo: String): Empleado?

    /**
     * Obtiene todos los empleados activos ordenados por apellido.
     * Retorna un Flow para observación reactiva.
     */
    @Query("SELECT * FROM empleados WHERE activo = 1 ORDER BY apellido ASC")
    fun getAllEmpleadosActivos(): Flow<List<Empleado>>

    /**
     * Obtiene todos los empleados (activos e inactivos) ordenados por apellido.
     * Retorna un Flow para observación reactiva.
     */
    @Query("SELECT * FROM empleados ORDER BY apellido ASC")
    fun getAllEmpleados(): Flow<List<Empleado>>

    /**
     * Da de baja lógica a un empleado (marca como inactivo).
     */
    @Query("UPDATE empleados SET activo = 0 WHERE legajo = :legajo")
    suspend fun darDeBajaEmpleado(legajo: String)
}
