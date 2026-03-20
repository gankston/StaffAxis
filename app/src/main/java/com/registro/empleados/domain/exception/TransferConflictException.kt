package com.registro.empleados.domain.exception

import com.registro.empleados.domain.model.Empleado

/**
 * Excepción lanzada cuando se intenta crear un empleado que ya existe en otro sector.
 * Contiene la información del empleado existente para permitir un traspaso confirmado.
 */
class TransferConflictException(
    val messageText: String,
    val existingEmployee: Empleado,
    val existingSectorName: String
) : Exception(messageText)
