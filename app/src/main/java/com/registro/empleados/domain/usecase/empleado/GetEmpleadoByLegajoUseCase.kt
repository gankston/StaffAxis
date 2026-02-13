package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.repository.EmpleadoRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener un empleado por su legajo.
 * Encapsula la lógica de negocio para buscar empleados.
 */
class GetEmpleadoByLegajoUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository
) {
    
    /**
     * Ejecuta el caso de uso para obtener un empleado por legajo.
     * @param legajo Legajo del empleado a buscar
     * @return Empleado encontrado o null si no existe
     */
    suspend operator fun invoke(legajo: String): Empleado? {
        if (legajo.isBlank()) {
            throw IllegalArgumentException("El legajo no puede estar vacío")
        }
        
        return empleadoRepository.getEmpleadoByLegajo(legajo)
    }
}
