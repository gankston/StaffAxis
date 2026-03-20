package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateSubmissionRequestDto(
    @SerializedName("employee_id") val employeeId: String,
    @SerializedName("date") val date: String, // YYYY-MM-DD
    @SerializedName("minutes_worked") val minutesWorked: Int? = null,
    @SerializedName("check_in") val checkIn: String? = null, // HH:MM
    @SerializedName("check_out") val checkOut: String? = null, // HH:MM
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("dni") val dni: String? = null,
    @SerializedName("encargado") val encargado: String? = null,
    @SerializedName("external_code") val externalCode: String? = null
)
