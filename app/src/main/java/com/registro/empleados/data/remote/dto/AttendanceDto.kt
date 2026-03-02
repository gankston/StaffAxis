package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de GET /approved.
 * Timestamps en unix epoch (segundos) como Long.
 */
data class AttendanceDto(
    @SerializedName("id") val id: String,
    @SerializedName("employee_id") val employeeId: String,
    @SerializedName("sector_id") val sectorId: String,
    @SerializedName("date") val date: String, // YYYY-MM-DD
    @SerializedName("minutes_worked") val minutesWorked: Int? = null,
    @SerializedName("check_in") val checkIn: String? = null, // HH:MM
    @SerializedName("check_out") val checkOut: String? = null, // HH:MM
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("updated_at") val updatedAt: Long,
    @SerializedName("deleted_at") val deletedAt: Long? = null
)
