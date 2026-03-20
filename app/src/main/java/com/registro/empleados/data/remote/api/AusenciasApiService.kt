package com.registro.empleados.data.remote.api

import com.registro.empleados.data.remote.dto.AbsenceResponseDto
import com.registro.empleados.data.remote.dto.AbsencesListResponseDto
import com.registro.empleados.data.remote.dto.CreateAbsenceRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interfaz de la API para las ausencias.
 *
 * Autenticación:
 *  - POST: el interceptor agrega X-Device-Token automáticamente
 *  - GET: el interceptor agrega X-Device-Token automáticamente, el servidor
 *         filtra por sector según ese token (Comportamiento A)
 */
interface AusenciasApiService {

    /**
     * ENDPOINT 1: Crear ausencia (uso exclusivo del celular).
     * El sector_id lo deduce el servidor a partir del X-Device-Token.
     */
    @POST("api/absences")
    suspend fun createAbsence(@Body request: CreateAbsenceRequestDto): Response<Unit>

    /**
     * ENDPOINT 2: Listar ausencias del día.
     * El servidor devuelve {"absences": [...]} → usar AbsencesListResponseDto.
     * Con X-Device-Token el servidor filtra por el sector del dispositivo.
     */
    @GET("api/absences")
    suspend fun getAbsences(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<AbsencesListResponseDto>
}
