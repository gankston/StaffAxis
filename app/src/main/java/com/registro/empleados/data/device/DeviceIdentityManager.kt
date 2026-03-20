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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val registerMutex = Mutex()

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
            registerMutex.withLock {
                doRegisterWhenConfigSaved(nombreEncargado, sectorWanted)
            }
        }

    private companion object {
        private const val REGISTER_RATE_LIMIT_MS = 30_000L
    }

    private suspend fun doRegisterWhenConfigSaved(nombreEncargado: String, sectorWanted: String) {
        if (!devicePrefs.getDeviceToken().isNullOrBlank()) {
            return
        }
        val now = System.currentTimeMillis()
        val lastAttempt = devicePrefs.getLastRegisterAttemptMillis()
        if (lastAttempt > 0 && (now - lastAttempt) < REGISTER_RATE_LIMIT_MS) {
            Log.i("StaffAxis", "REGISTER_SKIPPED rateLimit")
            return
        }
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
                Log.e("StaffAxis", "registerDevice -> sector_id not resolved (sectors empty/invalid)")
                return
            }
            devicePrefs.setLastRegisterAttemptMillis(System.currentTimeMillis())
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
                val body = response.body()
                if (code == 202 || body?.pending == true) {
                    Log.i("StaffAxis", "registerDevice -> 202 pending, retry later")
                    return
                }
                val token = body?.token
                if (!token.isNullOrBlank()) {
                    devicePrefs.setDeviceCredentials(token, deviceId)
                    Log.i("StaffAxis", "registerDevice success status=$code tokenSaved=true")
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

    private suspend fun resolveSectorId(wantedSectorId: String): String? {
        return try {
            Log.i("StaffAxis", "GET_SECTORS start")
            val response = sectorsApiService.getSectors()
            val status = response.code()
            val ct = response.headers()["Content-Type"] ?: ""
            Log.i("StaffAxis", "GET_SECTORS status=$status ct=$ct")
            if (!response.isSuccessful) {
                Log.e("StaffAxis", "GET_SECTORS fail status=$status")
                return null
            }
            val dto = response.body()
            val sectors = dto?.sectors ?: emptyList()
            val ids = sectors.map { it.id }.filter { it.isNotBlank() }
            Log.i("StaffAxis", "getSectors OK sectorsCount=${sectors.size} ids=$ids")
            when {
                sectors.size == 1 -> {
                    val id = sectors.first().id
                    if (id.isBlank()) {
                        Log.e("StaffAxis", "resolveSector FAILED single sector has empty id")
                        return null
                    }
                    Log.i("StaffAxis", "resolveSector fallback single sector_id=$id")
                    id
                }
                else -> {
                    // Varios sectores: buscar por nombre (wantedSectorId es el nombre del sector)
                    val match = sectors.find { it.name.equals(wantedSectorId, ignoreCase = true) }
                    if (match != null && match.id.isNotBlank()) {
                        Log.i("StaffAxis", "resolveSector matched by name '${match.name}' -> sector_id=${match.id}")
                        match.id
                    } else {
                        Log.e("StaffAxis", "resolveSector FAILED no match for wanted='$wantedSectorId' among ${sectors.size} sectors")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("StaffAxis", "GET_SECTORS fail type=${e.javaClass.simpleName} msg=${e.message}", e)
            null
        }
    }
}
