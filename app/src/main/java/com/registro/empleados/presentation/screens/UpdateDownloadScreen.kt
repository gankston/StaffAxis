package com.registro.empleados.presentation.screens

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.registro.empleados.presentation.utils.UpdateInfo
import kotlinx.coroutines.delay
import java.io.File

/**
 * Pantalla de descarga de actualización.
 * Permite descargar e instalar el APK sin salir de la app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDownloadScreen(
    updateInfo: UpdateInfo,
    onClose: () -> Unit,
    onInstall: () -> Unit
) {
    val context = LocalContext.current
    var downloadStatus by remember { mutableStateOf("Iniciando descarga...") }
    var showInstallButton by remember { mutableStateOf(false) }
    var downloadedApkPath by remember { mutableStateOf<String?>(null) }
    var downloadId by remember { mutableStateOf(-1L) }

    // Iniciar descarga automáticamente
    LaunchedEffect(Unit) {
        try {
            downloadId = startDownload(context, updateInfo.apkUrl)
            if (downloadId != -1L) {
                downloadStatus = "Descargando..."
                // Verificar estado de descarga periódicamente
                checkDownloadStatus(context, downloadId) { status, path ->
                    downloadStatus = status
                    if (path != null) {
                        downloadedApkPath = path
                        showInstallButton = true
                    }
                }
            } else {
                downloadStatus = "Error al iniciar descarga"
            }
        } catch (e: Exception) {
            downloadStatus = "Error: ${e.message}"
        }
    }

    // Manejar botón atrás
    BackHandler(enabled = !updateInfo.mandatory) {
        onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A1B9A),
                        Color(0xFF4A148C),
                        Color(0xFF1E1E2E)
                    )
                )
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .align(Alignment.Center)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A223C)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Actualización disponible",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Versión ${updateInfo.versionName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFB0B0B0)
                        )
                    }
                    if (!updateInfo.mandatory) {
                        IconButton(onClick = onClose) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notas de la actualización
                if (!updateInfo.notes.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF3A2F4C)
                        )
                    ) {
                        Text(
                            text = updateInfo.notes,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Información de descarga
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E2E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = downloadStatus,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (showInstallButton) Color(0xFF4CAF50) else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        if (!showInstallButton) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF6A1B9A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de instalar
                if (showInstallButton && downloadedApkPath != null) {
                    Button(
                        onClick = {
                            installApk(context, downloadedApkPath!!, onInstall)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text(
                            text = "Instalar Actualización",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Inicia la descarga del APK usando DownloadManager.
 */
private fun startDownload(context: Context, apkUrl: String): Long {
    return try {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val fileName = "StaffAxis_update.apk"
        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Descargando StaffAxis")
            .setDescription("Descargando actualización...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        downloadManager.enqueue(request)
    } catch (e: Exception) {
        android.util.Log.e("UpdateDownload", "Error al iniciar descarga", e)
        -1L
    }
}

/**
 * Verifica periódicamente el estado de la descarga.
 */
private suspend fun checkDownloadStatus(
    context: Context,
    downloadId: Long,
    onStatusUpdate: (String, String?) -> Unit
) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val query = DownloadManager.Query().setFilterById(downloadId)

    while (true) {
        delay(1000) // Verificar cada segundo

        val cursor = downloadManager.query(query)
        try {
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        val uriString = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                        onStatusUpdate("Descarga completada", uriString)
                        break
                    }
                    DownloadManager.STATUS_FAILED -> {
                        onStatusUpdate("Error en la descarga", null)
                        break
                    }
                    DownloadManager.STATUS_RUNNING -> {
                        val bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val totalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (totalBytes > 0) {
                            val progress = (bytesDownloaded * 100 / totalBytes)
                            onStatusUpdate("Descargando... $progress%", null)
                        } else {
                            onStatusUpdate("Descargando...", null)
                        }
                    }
                    DownloadManager.STATUS_PENDING -> {
                        onStatusUpdate("Preparando descarga...", null)
                    }
                    DownloadManager.STATUS_PAUSED -> {
                        onStatusUpdate("Descarga pausada", null)
                    }
                }
            }
        } finally {
            cursor.close()
        }
    }
}

/**
 * Instala el APK descargado.
 */
private fun installApk(context: Context, apkPath: String, onInstall: () -> Unit) {
    try {
        val uri = Uri.parse(apkPath)
        var file: File? = null

        // Intentar obtener el archivo real
        if (apkPath.startsWith("file://")) {
            file = File(apkPath.removePrefix("file://"))
        } else if (apkPath.startsWith("content://")) {
            // Para content://, usar la ruta de Downloads
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            file = File(downloadsDir, "StaffAxis_update.apk")
        }

        // Si no encontramos el archivo, intentar con la ruta directa
        if (file == null || !file.exists()) {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            file = File(downloadsDir, "StaffAxis_update.apk")
        }

        if (file == null || !file.exists()) {
            android.util.Log.e("UpdateDownload", "El archivo APK no existe: ${file?.absolutePath}")
            return
        }

        val installUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(installUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(installIntent)
        onInstall()
    } catch (e: Exception) {
        android.util.Log.e("UpdateDownload", "Error al instalar APK", e)
    }
}
