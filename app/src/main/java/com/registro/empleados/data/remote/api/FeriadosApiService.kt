package com.registro.empleados.data.remote.api

import com.registro.empleados.data.remote.dto.FeriadosResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz para el servicio de la API de feriados argentinos.
 * Utiliza la API de nolaborables.com.ar
 */
interface FeriadosApiService {
    
    /**
     * Obtiene los feriados nacionales para un año específico.
     * @param año Año para consultar los feriados
     * @return Lista de feriados nacionales
     */
    @GET("api/v2/feriados/{año}")
    suspend fun getFeriados(@Path("año") año: Int): Response<List<FeriadosResponseDto>>
    
    /**
     * Obtiene los feriados nacionales para el año actual.
     * @return Lista de feriados nacionales
     */
    @GET("api/v2/feriados/")
    suspend fun getFeriadosActuales(): Response<List<FeriadosResponseDto>>
}
