package com.registro.empleados.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "app_config",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_NOMBRE_ENCARGADO = "nombre_encargado"
        private const val KEY_SECTOR = "sector_seleccionado"
        private const val KEY_ES_PRIMERA_VEZ = "es_primera_vez"
    }
    
    fun guardarConfiguracion(nombreEncargado: String, sector: String) {
        prefs.edit().apply {
            putString(KEY_NOMBRE_ENCARGADO, nombreEncargado)
            putString(KEY_SECTOR, sector)
            putBoolean(KEY_ES_PRIMERA_VEZ, false)
            apply()
        }
    }
    
    fun esPrimeraVez(): Boolean {
        return prefs.getBoolean(KEY_ES_PRIMERA_VEZ, true)
    }
    
    fun getNombreEncargado(): String {
        return prefs.getString(KEY_NOMBRE_ENCARGADO, "") ?: ""
    }
    
    fun getSectorSeleccionado(): String {
        return prefs.getString(KEY_SECTOR, "") ?: ""
    }
    
    fun tieneConfiguracion(): Boolean {
        return !esPrimeraVez() && 
               getNombreEncargado().isNotBlank() && 
               getSectorSeleccionado().isNotBlank()
    }
}
