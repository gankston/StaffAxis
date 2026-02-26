package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.repository.EmpleadoRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para insertar un nuevo empleado.
 * Encapsula la lógica de negocio y validaciones para crear empleados.
 */
class InsertEmpleadoUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository
) {
    
    /**
     * Ejecuta el caso de uso para insertar un nuevo empleado.
     * @param legajo Legajo único del empleado
     * @param nombreCompleto Nombre completo del empleado
     * @param sector Sector donde trabaja el empleado
     * @param fechaIngreso Fecha de ingreso del empleado
     * @throws IllegalArgumentException si los datos no son válidos
     */
    suspend operator fun invoke(
        legajo: String,
        nombreCompleto: String,
        sector: String,
        fechaIngreso: LocalDate
    ) {
        // Validaciones de negocio
        validateInputs(legajo, nombreCompleto, sector, fechaIngreso)
        
        val empleado = Empleado(
            legajo = legajo.trim().uppercase(),
            nombreCompleto = nombreCompleto.trim(),
            sector = sector.trim(),
            fechaIngreso = fechaIngreso,
            activo = true,
            fechaCreacion = LocalDate.now()
        )
        
        empleadoRepository.insertEmpleado(empleado)
    }
    
    /**
     * Valida los datos de entrada para crear un empleado.
     */
    private suspend fun validateInputs(
        legajo: String,
        nombreCompleto: String,
        sector: String,
        fechaIngreso: LocalDate
    ) {
        if (legajo.isBlank()) {
            throw IllegalArgumentException("El DNI es obligatorio")
        }
        
        if (nombreCompleto.isBlank()) {
            throw IllegalArgumentException("El nombre completo es obligatorio")
        }
        
        if (sector.isBlank()) {
            throw IllegalArgumentException("El sector es obligatorio")
        }
        
        if (fechaIngreso.isAfter(LocalDate.now())) {
            throw IllegalArgumentException("La fecha de ingreso no puede ser futura")
        }
        
        // Verificar si ya existe un empleado con ese legajo (solo si no está vacío)
        if (legajo.isNotBlank() && empleadoRepository.existeEmpleadoConLegajo(legajo.trim().uppercase())) {
            throw IllegalArgumentException("Ya existe un empleado con el legajo $legajo")
        }
    }
}
