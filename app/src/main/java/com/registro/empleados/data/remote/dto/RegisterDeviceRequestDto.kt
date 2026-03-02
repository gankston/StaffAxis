package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterDeviceRequestDto(
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("sector_id") val sectorId: String,
    @SerializedName("encargado_name") val encargadoName: String
)
