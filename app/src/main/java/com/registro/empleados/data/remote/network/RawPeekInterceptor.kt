package com.registro.empleados.data.remote.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class RawPeekInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        try {
            val contentType = response.header("content-type")
            val peekBody = response.peekBody(512).string()
                .replace("\n", "\\n")
                .take(300)

            Log.i(
                "StaffAxis",
                "HTTP ${request.method} ${request.url} -> ${response.code} ct=$contentType peek=$peekBody"
            )
        } catch (e: Exception) {
            Log.e("StaffAxis", "Interceptor error: ${e.message}")
        }

        return response
    }
}
