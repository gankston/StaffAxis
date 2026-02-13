package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.repository.EmpleadoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para buscar empleados por diferentes criterios.
 */
class BuscarEmpleadosUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository
) {
    
    /**
     * Busca empleados por legajo.
     */
    suspend fun buscarPorLegajo(legajo: String): Empleado? {
        if (legajo.isBlank()) return null
        return empleadoRepository.getEmpleadoByLegajo(legajo)
    }
    
    /**
     * Busca empleados por nombre y/o apellido.
     */
    suspend fun buscarPorNombre(nombre: String, apellido: String): Flow<List<Empleado>> {
        return when {
            nombre.isNotBlank() && apellido.isNotBlank() -> {
                empleadoRepository.buscarEmpleadosPorNombreYApellido(nombre, apellido)
            }
            nombre.isNotBlank() -> {
                empleadoRepository.buscarEmpleadosPorNombre(nombre)
            }
            apellido.isNotBlank() -> {
                empleadoRepository.buscarEmpleadosPorApellido(apellido)
            }
            else -> {
                empleadoRepository.getAllEmpleadosActivos()
            }
        }
    }
    
    /**
     * Busca empleados por criterio general (legajo, nombre o apellido).
     */
    suspend fun buscarEmpleados(criterio: String): Flow<List<Empleado>> {
        if (criterio.isBlank()) {
            return empleadoRepository.getAllEmpleadosActivos()
        }
        
        // Si el criterio parece un legajo (solo números o letras/números), buscar por legajo
        if (criterio.matches(Regex("^[A-Za-z0-9]+$"))) {
            val empleado = empleadoRepository.getEmpleadoByLegajo(criterio)
            return if (empleado != null) {
                kotlinx.coroutines.flow.flowOf(listOf(empleado))
            } else {
                empleadoRepository.buscarEmpleadosPorNombre(criterio)
            }
        }
        
        // Buscar por nombre o apellido
        return empleadoRepository.buscarEmpleadosPorNombre(criterio)
    }
}
