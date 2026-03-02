package com.registro.empleados.data.remote.api

import com.registro.empleados.data.remote.dto.RegisterDeviceRequestDto
import com.registro.empleados.data.remote.dto.RegisterDeviceResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API para autenticación de dispositivo con el backend StaffAxis.
 */
interface AuthApiService {

    @POST("api/auth/device/register")
    suspend fun registerDevice(
        @Body body: RegisterDeviceRequestDto
    ): Response<RegisterDeviceResponseDto>
}
