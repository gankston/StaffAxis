package com.registro.empleados.data.remote.api

import com.registro.empleados.data.remote.dto.CreateEmployeeRequestDto
import com.registro.empleados.data.remote.dto.EmployeeDto
import com.registro.empleados.data.remote.dto.EmployeesResponseDto
import com.registro.empleados.data.remote.dto.UpdateEmployeeRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API para empleados del backend StaffAxis.
 * GET /api/employees?sector_id={id}
 * POST /api/employees — crea empleado y devuelve el objeto con id.
 * PUT /api/employees/{id} — actualiza datos del empleado.
 */
interface EmployeesApiService {

    @GET("api/employees")
    suspend fun getEmployees(
        @Query("sector_id") sectorId: String
    ): Response<EmployeesResponseDto>

    @POST("api/employees")
    suspend fun createEmployee(
        @Body body: CreateEmployeeRequestDto
    ): Response<EmployeeDto>

    @PUT("api/employees/{id}")
    suspend fun updateEmployee(
        @Path("id") id: String,
        @Body body: UpdateEmployeeRequestDto
    ): Response<EmployeeDto>
}

