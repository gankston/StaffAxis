package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de una ausencia desde el API de producción.
 */
data class AbsenceResponseDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("employee_id")
    val employeeId: String,

    @SerializedName("sector_id")
    val sectorId: String,

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("end_date")
    val endDate: String,

    @SerializedName("reason")
    val reason: String? = null,

    @SerializedName("observations")
    val observations: String? = null,

    @SerializedName("is_justified")
    val isJustified: Int = 0,

    @SerializedName("created_at")
    val createdAt: String? = null
)

/**
 * Wrapper para la respuesta del GET /api/absences que devuelve {"absences": [...]}.
 */
data class AbsencesListResponseDto(
    @SerializedName("absences")
    val absences: List<AbsenceResponseDto> = emptyList()
)

