package com.registro.empleados.domain.repository

import com.registro.empleados.domain.model.Empleado
import kotlinx.coroutines.flow.Flow

/**
 * Interface del repositorio para operaciones con empleados.
 * Define el contrato que debe implementar la capa de datos.
 */
interface EmpleadoRepository {
    
    /**
     * Obtiene todos los empleados activos.
     * @return Flow con la lista de empleados activos
     */
    suspend fun getAllEmpleadosActivos(): Flow<List<Empleado>>

    /**
     * Obtiene todos los empleados (activos e inactivos).
     * @return Flow con la lista de todos los empleados
     */
    suspend fun getAllEmpleados(): Flow<List<Empleado>>

    /**
     * Obtiene un empleado por su legajo.
     * @param legajo Legajo del empleado
     * @return Empleado encontrado o null si no existe
     */
    suspend fun getEmpleadoByLegajo(legajo: String): Empleado?

    /**
     * Inserta un nuevo empleado.
     * @param empleado Empleado a insertar
     * @throws IllegalArgumentException si el legajo ya existe
     */
    suspend fun insertEmpleado(empleado: Empleado)

    /**
     * Actualiza un empleado existente.
     * @param empleado Empleado con los datos actualizados
     */
    suspend fun updateEmpleado(empleado: Empleado)

    /**
     * Da de baja lógica a un empleado (marca como inactivo).
     * @param legajo Legajo del empleado a dar de baja
     */
    suspend fun darDeBajaEmpleado(legajo: String)
    
    /**
     * Actualiza el estado activo de un empleado por ID.
     * @param id ID del empleado
     * @param activo Nuevo estado
     */
    suspend fun updateEstadoEmpleado(id: Long, activo: Boolean)

    /**
     * Verifica si ya existe un empleado con el legajo especificado.
     * @param legajo Legajo a verificar
     * @return true si existe, false si no existe
     */
    suspend fun existeEmpleadoConLegajo(legajo: String?): Boolean
    
    /**
     * Busca empleados por nombre.
     * @param nombre Nombre a buscar (búsqueda parcial)
     * @return Flow con la lista de empleados que coinciden
     */
    suspend fun buscarEmpleadosPorNombre(nombre: String): Flow<List<Empleado>>
    
    /**
     * Busca empleados por apellido.
     * @param apellido Apellido a buscar (búsqueda parcial)
     * @return Flow con la lista de empleados que coinciden
     */
    suspend fun buscarEmpleadosPorApellido(apellido: String): Flow<List<Empleado>>
    
    /**
     * Busca empleados por nombre y apellido.
     * @param nombre Nombre a buscar
     * @param apellido Apellido a buscar
     * @return Flow con la lista de empleados que coinciden
     */
    suspend fun buscarEmpleadosPorNombreYApellido(nombre: String, apellido: String): Flow<List<Empleado>>

    /**
     * Busca empleados usando una query combinada.
     * Busca por legajo, nombre completo o apellido + nombre.
     * @param query Texto de búsqueda
     * @return Flow con la lista de empleados que coinciden
     */
    suspend fun buscarEmpleados(query: String): Flow<List<Empleado>>

    /**
     * Elimina completamente un empleado de la base de datos por su legajo.
     * ¡CUIDADO! Esta operación es irreversible.
     * @param legajo Legajo del empleado a eliminar
     */
    suspend fun deleteEmpleado(legajo: String)
    
    suspend fun quitarLegajoEmpleado(id: Long)

    /**
     * Elimina todos los empleados de la base de datos.
     * ¡CUIDADO! Esta operación es irreversible.
     */
    suspend fun deleteAllEmpleados()
}
