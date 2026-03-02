package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApprovedResponseDto(
    @SerializedName("attendances") val attendances: List<AttendanceDto>
)
