package com.registro.empleados.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.syncStateDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "sync_state_prefs"
)

/**
 * Estado de sincronización con el backend.
 */
@Singleton
class SyncStatePrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.syncStateDataStore

    suspend fun getLastApprovedSyncAt(): Long {
        return dataStore.data.first()[KEY_LAST_APPROVED_SYNC_AT] ?: 0L
    }

    suspend fun setLastApprovedSyncAt(value: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_LAST_APPROVED_SYNC_AT] = value
        }
    }

    companion object {
        private val KEY_LAST_APPROVED_SYNC_AT = longPreferencesKey("last_approved_sync_at")
    }
}
