package com.registro.empleados.presentation.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Clase utilitaria para verificar actualizaciones de la aplicación.
 * Consulta un JSON remoto y muestra un diálogo si hay una versión nueva disponible.
 */
object UpdateChecker {
    
    private const val TAG = "UpdateChecker"
    private const val VERSION_JSON_URL = "https://gankston.github.io/staffaxis-updates/version.json"
    private const val TIMEOUT_SECONDS = 10L
    
    /**
     * Verifica si hay una actualización disponible.
     * Se ejecuta en background y muestra el diálogo en el hilo principal si es necesario.
     * 
     * @param context Contexto de la aplicación
     * @param scope CoroutineScope para ejecutar la verificación en background
     * @param onUpdateAvailable Callback cuando hay una actualización disponible (recibe UpdateInfo)
     */
    fun checkForUpdates(
        context: Context, 
        scope: CoroutineScope,
        onUpdateAvailable: (UpdateInfo) -> Unit
    ) {
        scope.launch {
            try {
                val updateInfo = fetchUpdateInfo()
                if (updateInfo != null) {
                    val localVersionCode = getLocalVersionCode(context)
                    if (updateInfo.versionCode > localVersionCode) {
                        withContext(Dispatchers.Main) {
                            showUpdateDialog(context, updateInfo, onUpdateAvailable)
                        }
                    } else {
                        Log.d(TAG, "La app está actualizada. Local: $localVersionCode, Remota: ${updateInfo.versionCode}")
                    }
                }
            } catch (e: Exception) {
                // Manejo silencioso de errores: no crashea la app
                Log.e(TAG, "Error al verificar actualizaciones", e)
            }
        }
    }
    
    /**
     * Obtiene la información de actualización desde el servidor remoto.
     * 
     * @return UpdateInfo si se pudo obtener, null en caso de error
     */
    private suspend fun fetchUpdateInfo(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
            
            val request = Request.Builder()
                .url(VERSION_JSON_URL)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e(TAG, "Error HTTP: ${response.code}")
                return@withContext null
            }
            
            val jsonString = response.body?.string()
            if (jsonString.isNullOrBlank()) {
                Log.e(TAG, "Respuesta vacía del servidor")
                return@withContext null
            }
            
            val json = JSONObject(jsonString)
            UpdateInfo(
                versionCode = json.getInt("versionCode").toLong(),
                versionName = json.getString("versionName"),
                apkUrl = json.getString("apkUrl"),
                mandatory = json.optBoolean("mandatory", false),
                notes = json.optString("notes", "").takeIf { it.isNotBlank() }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener información de actualización", e)
            null
        }
    }
    
    /**
     * Obtiene el versionCode de la aplicación instalada.
     * 
     * @param context Contexto de la aplicación
     * @return versionCode como Long
     */
    private fun getLocalVersionCode(context: Context): Long {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener versionCode local", e)
            0L
        }
    }
    
    /**
     * Muestra el diálogo de actualización.
     * 
     * @param context Contexto de la aplicación
     * @param updateInfo Información de la actualización disponible
     * @param onUpdateAvailable Callback cuando el usuario quiere actualizar (se ejecuta en main thread)
     */
    private fun showUpdateDialog(
        context: Context, 
        updateInfo: UpdateInfo,
        onUpdateAvailable: (UpdateInfo) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
            .setTitle("Actualización disponible: ${updateInfo.versionName}")
            .setMessage(updateInfo.notes ?: "Hay una nueva versión disponible.")
            .setPositiveButton("Actualizar") { _, _ ->
                onUpdateAvailable(updateInfo)
            }
        
        // Solo mostrar botón "Más tarde" si no es obligatoria
        if (!updateInfo.mandatory) {
            builder.setNegativeButton("Más tarde", null)
        }
        
        val dialog = builder.create()
        
        // Si es obligatoria, no permitir cancelar el diálogo
        if (updateInfo.mandatory) {
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
        }
        
        dialog.show()
    }
}

/**
 * Data class que representa la información de una actualización.
 */
data class UpdateInfo(
    val versionCode: Long,
    val versionName: String,
    val apkUrl: String,
    val mandatory: Boolean,
    val notes: String?
)

