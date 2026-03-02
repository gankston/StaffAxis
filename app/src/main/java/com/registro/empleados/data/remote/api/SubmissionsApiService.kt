package com.registro.empleados.data.remote.api

import com.registro.empleados.data.remote.dto.ApprovedResponseDto
import com.registro.empleados.data.remote.dto.CreateSubmissionRequestDto
import com.registro.empleados.data.remote.dto.SubmissionResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * API para submissions y approved del backend StaffAxis.
 * Requiere X-Device-Token / Authorization: Bearer (device_token).
 */
interface SubmissionsApiService {

    @POST("api/submissions")
    suspend fun createSubmission(
        @Body body: CreateSubmissionRequestDto
    ): Response<SubmissionResponseDto>

    /** Raw response sin Gson para diagnóstico de malformed JSON. */
    @POST("api/submissions")
    suspend fun createSubmissionRaw(@Body body: CreateSubmissionRequestDto): Response<ResponseBody>

    @GET("api/approved")
    suspend fun getApproved(@Query("since") since: Long): Response<ApprovedResponseDto>
}
