package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Body para PUT /api/employees/{id}.
 */
data class UpdateEmployeeRequestDto(
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("dni") val dni: String? = null,
    @SerializedName("external_code") val externalCode: String? = null,
    @SerializedName("sector_id") val sectorId: String? = null,
    @SerializedName("is_active") val isActive: Boolean? = null
)
