package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.repository.EmpleadoRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para actualizar un empleado existente.
 * Encapsula la lógica de negocio para actualizar empleados.
 */
class UpdateEmpleadoUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository
) {
    
    /**
     * Ejecuta el caso de uso para actualizar un empleado.
     * @param empleado Empleado con los datos actualizados
     * @throws IllegalArgumentException si los datos no son válidos
     */
    suspend operator fun invoke(empleado: Empleado) {
        android.util.Log.d("UpdateEmpleadoUseCase", "=== ACTUALIZANDO EMPLEADO ===")
        android.util.Log.d("UpdateEmpleadoUseCase", "ID: ${empleado.id}")
        android.util.Log.d("UpdateEmpleadoUseCase", "Legajo: ${empleado.legajo}")
        android.util.Log.d("UpdateEmpleadoUseCase", "Nombre: ${empleado.nombreCompleto}")
        android.util.Log.d("UpdateEmpleadoUseCase", "Sector: ${empleado.sector}")
        
        // Validaciones de negocio
        validateInputs(empleado)
        
        empleadoRepository.updateEmpleado(empleado)
        
        android.util.Log.d("UpdateEmpleadoUseCase", "✅ Empleado actualizado exitosamente")
    }
    
    /**
     * Valida los datos de entrada para actualizar un empleado.
     */
    private suspend fun validateInputs(empleado: Empleado) {
        if (empleado.nombreCompleto.isBlank()) {
            throw IllegalArgumentException("El nombre completo no puede estar vacío")
        }
        
        if (empleado.sector.isBlank()) {
            throw IllegalArgumentException("El sector no puede estar vacío")
        }
        
        // Verificar que el empleado existe (por ID, no por legajo ya que puede ser null o cambiarse)
        if (empleado.id == 0L) {
            throw IllegalArgumentException("El empleado no tiene un ID válido")
        }
    }
}
