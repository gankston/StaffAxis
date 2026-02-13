package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.repository.EmpleadoRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso simple para buscar empleados por legajo, nombre o apellido.
 */
class BuscarEmpleadoSimpleUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository
) {
    
    /**
     * Busca un empleado por legajo o nombre completo.
     * Prioriza legajo si está disponible, sino busca por nombre completo.
     */
    suspend operator fun invoke(
        legajo: String = "",
        nombreCompleto: String = ""
    ): Empleado? {
        // Si hay legajo, buscar por legajo (búsqueda exacta)
        if (legajo.isNotBlank()) {
            return empleadoRepository.getEmpleadoByLegajo(legajo)
        }
        
        // Si no hay legajo, buscar por nombre completo
        if (nombreCompleto.isNotBlank()) {
            val empleados = empleadoRepository.buscarEmpleados(nombreCompleto).first()
            return empleados.firstOrNull()
        }
        
        return null
    }
}
