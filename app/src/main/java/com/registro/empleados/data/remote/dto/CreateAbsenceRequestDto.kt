package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la solicitud de creación de una ausencia.
 */
data class CreateAbsenceRequestDto(
    @SerializedName("employee_id")
    val employeeId: String,
    
    @SerializedName("start_date")
    val startDate: String, // YYYY-MM-DD
    
    @SerializedName("end_date")
    val endDate: String, // YYYY-MM-DD
    
    @SerializedName("reason")
    val reason: String? = null,
    
    @SerializedName("observations")
    val observations: String? = null,
    
    @SerializedName("is_justified")
    val isJustified: Boolean = false
)
