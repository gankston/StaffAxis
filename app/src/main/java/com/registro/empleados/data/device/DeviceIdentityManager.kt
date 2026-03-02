package com.registro.empleados.data.device

import android.util.Log
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.data.local.preferences.DevicePrefs
import com.registro.empleados.data.remote.api.AuthApiService
import com.google.gson.Gson
import com.registro.empleados.data.remote.api.SectorsApiService
import com.registro.empleados.data.remote.api.SectorsResponseDto
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
     * - Usa sector_id persistido si existe; si no, resuelve vía GET /api/sectors
     * - Resolución: match por sector.id (exacto); si wanted vacío y solo 1 sector, usa ese
     * - Guarda el token y sector_id en prefs
     */
    suspend fun registerWhenConfigSaved(nombreEncargado: String, sectorWanted: String) =
        withContext(Dispatchers.IO) {
            Log.i("StaffAxis", "registerDevice -> start")
            try {
                val deviceId = devicePrefs.ensureDeviceId()
                var sectorId = appPreferences.getSectorId()
                if (sectorId.isNullOrBlank()) {
                    sectorId = resolveSectorId(sectorWanted)
                    if (sectorId != null) {
                        appPreferences.setSectorId(sectorId)
                    }
                }
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
                        Log.i("StaffAxis", "registerDevice success (200) token saved")
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

    private suspend fun resolveSectorId(wanted: String): String? {
        return try {
            val response = sectorsApiService.getSectors()
            Log.i("StaffAxis", "GET sectors url=" + response.raw().request.url)
            Log.i("StaffAxis", "GET sectors status=" + response.code())
            val raw = response.body()?.string()
            Log.i("StaffAxis", "GET sectors raw=" + (raw?.take(300) ?: "null"))
            response.errorBody()?.string()?.let { err ->
                Log.i("StaffAxis", "GET sectors errorBody=" + err.take(300))
            }
            if (!response.isSuccessful) {
                Log.e("StaffAxis", "getSectors fail code=${response.code()}")
                return null
            }
            val dto = raw?.let { Gson().fromJson(it, SectorsResponseDto::class.java) }
            val sectors = dto?.sectors ?: emptyList()
            val ids = sectors.map { it.id }
            Log.i("StaffAxis", "getSectors OK count=${sectors.size} ids=$ids")
            Log.i("StaffAxis", "resolveSector wanted=$wanted")
            val resolved = when {
                sectors.isEmpty() -> null
                wanted.isNotBlank() -> {
                    sectors.find { it.id == wanted }?.id
                        ?: sectors.find { it.name.equals(wanted, ignoreCase = true) }?.id
                }
                sectors.size == 1 -> sectors.first().id
                else -> null
            }
            if (resolved != null) {
                Log.i("StaffAxis", "resolveSector OK sector_id=$resolved")
            }
            resolved
        } catch (e: Exception) {
            Log.e("StaffAxis", "registerDevice -> fail code=-1 msg=get sectors ${e.javaClass.simpleName}", e)
            null
        }
    }
}
