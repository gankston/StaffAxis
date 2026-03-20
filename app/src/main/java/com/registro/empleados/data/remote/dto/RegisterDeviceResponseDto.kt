package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterDeviceResponseDto(
    @SerializedName("token") val token: String? = null,
    @SerializedName("pending") val pending: Boolean? = null
)
