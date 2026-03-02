package com.registro.empleados.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.deviceDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "device_prefs"
)

/**
 * Almacena device_token y device_id en DataStore Preferences.
 * Se usa para autenticación con el backend API.
 */
@Singleton
class DevicePrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.deviceDataStore

    /**
     * Asegura que exista device_id. Si no existe, genera UUID y lo persiste.
     * @return device_id actual o recién creado
     */
    suspend fun ensureDeviceId(): String {
        val existing = getDeviceId()
        if (!existing.isNullOrBlank()) return existing
        val newId = UUID.randomUUID().toString()
        dataStore.edit { prefs ->
            prefs[KEY_DEVICE_ID] = newId
        }
        return newId
    }

    /**
     * Guarda device_token y device_id (tras login/register de dispositivo).
     */
    suspend fun setDeviceCredentials(deviceToken: String, deviceId: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DEVICE_TOKEN] = deviceToken
            prefs[KEY_DEVICE_ID] = deviceId
        }
    }

    /**
     * Obtiene el device_token (suspend).
     */
    suspend fun getDeviceToken(): String? {
        return dataStore.data.first()[KEY_DEVICE_TOKEN]?.takeIf { it.isNotBlank() }
    }

    /**
     * Obtiene el device_id (suspend).
     */
    suspend fun getDeviceId(): String? {
        return dataStore.data.first()[KEY_DEVICE_ID]?.takeIf { it.isNotBlank() }
    }

    /**
     * Obtiene el device_token de forma síncrona (para interceptores OkHttp).
     */
    fun getDeviceTokenSync(): String? = runBlocking { getDeviceToken() }

    /**
     * Limpia las credenciales del dispositivo (logout).
     */
    suspend fun clearDeviceCredentials() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_DEVICE_TOKEN)
            prefs.remove(KEY_DEVICE_ID)
        }
    }

    companion object {
        private val KEY_DEVICE_TOKEN = stringPreferencesKey("device_token")
        private val KEY_DEVICE_ID = stringPreferencesKey("device_id")
    }
}
