package com.registro.empleados.data.remote.api

import com.registro.empleados.data.remote.dto.SectorDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

/**
 * API para sectores del backend StaffAxis.
 */
interface SectorsApiService {

    @GET("api/sectors")
    suspend fun getSectors(): Response<ResponseBody>
}

data class SectorsResponseDto(
    val sectors: List<SectorDto>
)
