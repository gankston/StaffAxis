package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Body para POST /api/employees.
 * El JSON debe incluir "dni" (valor capturado del campo DNI del formulario).
 */
data class CreateEmployeeRequestDto(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("dni") val dni: String?,  // Valor del EditText/campo DNI; serializado como "dni" en JSON
    @SerializedName("sector_id") val sectorId: String,
    @SerializedName("force_transfer") val forceTransfer: Boolean = false
)
