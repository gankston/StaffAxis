package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SectorDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)
