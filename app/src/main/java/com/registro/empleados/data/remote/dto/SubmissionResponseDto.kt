package com.registro.empleados.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del backend para POST /api/submissions.
 * Solo usamos status code en PushOutboxWorker; el body se deserializa pero no se usa.
 */
data class SubmissionResponseDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("status") val status: String? = null
)
