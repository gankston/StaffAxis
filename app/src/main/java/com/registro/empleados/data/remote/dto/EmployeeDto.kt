package com.registro.empleados.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/** Respuesta de GET /api/employees y de POST /api/employees (RETURNING). Misma estructura que la tabla. */
@Keep
data class EmployeeDto(
    @SerializedName("id") val id: String,
    @SerializedName("sector_id") val sectorId: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("dni") val dni: String? = null,  // Debe coincidir exactamente con la columna "dni" de la BD
    @SerializedName("external_code") val externalCode: String? = null,
    @SerializedName("is_active") val isActive: Boolean
)

@Keep
data class EmployeesResponseDto(
    @SerializedName("employees") val employees: List<EmployeeDto> = emptyList()
)

