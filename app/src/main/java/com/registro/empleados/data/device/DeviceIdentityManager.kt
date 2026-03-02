package com.registro.empleados.data.device

import android.util.Log
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.data.local.preferences.DevicePrefs
import com.registro.empleados.data.remote.api.AuthApiService
import com.registro.empleados.data.remote.api.SectorsApiService
import com.registro.empleados.data.remote.dto.RegisterDeviceRequestDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Gestiona la identidad del dispositivo:
 * - Genera y persiste device_id al primer arranque
 * - Registra el dispositivo en el backend cuando el usuario elige encargado/sector
 * - Guarda el token devuelto en DevicePrefs para autenticación posterior
 */
@Singleton
class DeviceIdentityManager @Inject constructor(
    private val devicePrefs: DevicePrefs,
    private val appPreferences: AppPreferences,
    private val authApiService: AuthApiService,
    private val sectorsApiService: SectorsApiService
) {

    /**
     * Asegura que device_id exista. Si no, genera UUID y lo guarda.
     * Llamar al arrancar la app (ej. en Application o primer acceso).
     */
    suspend fun ensureDeviceId(): String = withContext(Dispatchers.IO) {
        devicePrefs.ensureDeviceId()
    }

    /**
     * Asegura que device_token exista antes de push/pull.
     * Si hay config (encargado+sector) pero no token, llama registerDevice.
     * @return true si hay token disponible, false si no (no ejecutar workers).
     */
    suspend fun ensureDeviceToken(): Boolean = withContext(Dispatchers.IO) {
        var token = devicePrefs.getDeviceToken()
        if (!token.isNullOrBlank()) return@withContext true
        val nombreEncargado = appPreferences.getNombreEncargado()
        val sector = appPreferences.getSectorSeleccionado()
        if (nombreEncargado.isBlank() || sector.isBlank()) {
            Log.i("StaffAxis", "ensureDeviceToken -> no config (encargado/sector), skip")
            return@withContext false
        }
        Log.i("StaffAxis", "ensureDeviceToken -> no token, forcing registerDevice")
        registerWhenConfigSaved(nombreEncargado, sector)
        token = devicePrefs.getDeviceToken()
        !token.isNullOrBlank()
    }

    /**
     * Registra el dispositivo en el backend con encargado y sector.
     * Se llama internamente cuando el usuario guarda configuración (encargado + sector).
     * - Resuelve sector_id a partir del nombre (consulta API de sectores)
     * - Si no hay match, usa el primer sector disponible
     * - Guarda el token devuelto en DataStore
     */
    suspend fun registerWhenConfigSaved(nombreEncargado: String, sectorName: String) =
        withContext(Dispatchers.IO) {
            Log.i("StaffAxis", "registerDevice -> start")
            try {
                val deviceId = devicePrefs.ensureDeviceId()
                val sectorId = resolveSectorId(sectorName)
                if (sectorId == null) {
                    Log.e("StaffAxis", "registerDevice -> fail code=-1 msg=sector_id not resolved")
                    return@withContext
                }
                val response = authApiService.registerDevice(
                    RegisterDeviceRequestDto(
                        deviceId = deviceId,
                        sectorId = sectorId,
                        encargadoName = nombreEncargado
                    )
                )
                val code = response.code()
                val msg = response.message().orEmpty()
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (!token.isNullOrBlank()) {
                        devicePrefs.setDeviceCredentials(token, deviceId)
                        Log.i("StaffAxis", "registerDevice -> success")
                    } else {
                        Log.e("StaffAxis", "registerDevice -> fail code=$code msg=empty token")
                    }
                } else {
                    Log.e("StaffAxis", "registerDevice -> fail code=$code msg=$msg")
                }
            } catch (e: Exception) {
                Log.e("StaffAxis", "registerDevice -> fail code=-1 msg=${e.javaClass.simpleName}", e)
            }
        }

    private suspend fun resolveSectorId(sectorName: String): String? {
        return try {
            val response = sectorsApiService.getSectors()
            if (!response.isSuccessful) return null
            val sectors = response.body()?.sectors ?: return null
            sectors
                .find { it.name.equals(sectorName, ignoreCase = true) }
                ?.id
                ?: sectors.firstOrNull()?.id
        } catch (e: Exception) {
            Log.e("StaffAxis", "registerDevice -> fail code=-1 msg=get sectors ${e.javaClass.simpleName}")
            null
        }
    }
}
