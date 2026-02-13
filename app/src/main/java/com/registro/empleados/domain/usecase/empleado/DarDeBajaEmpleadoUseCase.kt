package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.repository.EmpleadoRepository
import javax.inject.Inject

/**
 * Caso de uso para eliminar completamente un empleado.
 * Encapsula la lógica de negocio para eliminar empleados de la base de datos.
 */
class DarDeBajaEmpleadoUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository
) {
    
    /**
     * Ejecuta el caso de uso para eliminar completamente un empleado.
     * @param legajo Legajo del empleado a eliminar
     * @throws IllegalArgumentException si el legajo no es válido o el empleado no existe
     */
    suspend operator fun invoke(legajo: String) {
        if (legajo.isBlank()) {
            throw IllegalArgumentException("El legajo no puede estar vacío")
        }
        
        // Verificar que el empleado existe
        val empleadoExistente = empleadoRepository.getEmpleadoByLegajo(legajo)
        if (empleadoExistente == null) {
            throw IllegalArgumentException("No existe un empleado con el legajo $legajo")
        }
        
        // Eliminar completamente el empleado de la base de datos
        empleadoRepository.deleteEmpleado(legajo)
    }
}
