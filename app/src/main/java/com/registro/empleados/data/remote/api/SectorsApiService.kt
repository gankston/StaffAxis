package com.registro.empleados.data.remote.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.registro.empleados.data.remote.dto.SectorDto
import retrofit2.Response
import retrofit2.http.GET

/**
 * API para sectores del backend StaffAxis.
 * Backend devuelve { "sectors": [{ "id": "...", "name": "..." }] }
 */
interface SectorsApiService {

    @GET("api/sectors")
    suspend fun getSectors(): Response<SectorsResponseDto>
}

@Keep
data class SectorsResponseDto(
    @SerializedName("sectors") val sectors: List<SectorDto> = emptyList()
)
