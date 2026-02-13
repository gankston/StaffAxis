package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.repository.EmpleadoRepository
import javax.inject.Inject

class UpdateEstadoEmpleadoUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository
) {
    suspend operator fun invoke(id: Long, activo: Boolean) {
        empleadoRepository.updateEstadoEmpleado(id, activo)
    }
}

