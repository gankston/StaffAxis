package com.registro.empleados.data.remote.api

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

data class SectorsResponseDto(
    val sectors: List<SectorDto>
)
