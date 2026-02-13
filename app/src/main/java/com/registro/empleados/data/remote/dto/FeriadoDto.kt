package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de la API de feriados argentinos.
 * Representa un feriado nacional.
 */
data class FeriadoDto(
    @SerializedName("motivo")
    val motivo: String,
    
    @SerializedName("tipo")
    val tipo: String,
    
    @SerializedName("info")
    val info: String,
    
    @SerializedName("dia")
    val dia: Int,
    
    @SerializedName("mes")
    val mes: Int,
    
    @SerializedName("id")
    val id: String
)

/**
 * DTO para la respuesta completa de la API de feriados.
 */
data class FeriadosResponseDto(
    @SerializedName("motivo")
    val motivo: String?,
    
    @SerializedName("tipo")
    val tipo: String?,
    
    @SerializedName("info")
    val info: String?,
    
    @SerializedName("dia")
    val dia: Int?,
    
    @SerializedName("mes")
    val mes: Int?,
    
    @SerializedName("id")
    val id: String?,
    
    @SerializedName("feriados")
    val feriados: List<FeriadoDto>
)
