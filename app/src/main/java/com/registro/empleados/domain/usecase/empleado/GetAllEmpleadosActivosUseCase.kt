package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.repository.EmpleadoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener todos los empleados activos.
 * Encapsula la lógica de negocio para recuperar empleados activos.
 */
class GetAllEmpleadosActivosUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository
) {
    
    /**
     * Ejecuta el caso de uso para obtener empleados activos.
     * @return Flow con la lista de empleados activos ordenados por apellido
     */
    suspend operator fun invoke(): Flow<List<Empleado>> {
        return empleadoRepository.getAllEmpleadosActivos()
    }
}
