@file:Suppress("DEPRECATION")

package com.registro.empleados.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.content.Intent as AndroidIntent
import android.net.Uri as AndroidUri
import androidx.core.content.FileProvider as AndroidFileProvider
import java.io.File as JavaFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import com.registro.empleados.R
import com.registro.empleados.presentation.viewmodel.ReportesViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Pantalla de reportes y exportación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: ReportesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Función para compartir archivos
    fun compartirArchivo(rutaArchivo: String) {
        try {
            val file = JavaFile(rutaArchivo)
            if (file.exists()) {
                val uri = AndroidFileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                        val mimeType = when (file.extension.lowercase()) {
                            "xls" -> "application/vnd.ms-excel"
                            "csv" -> "text/csv"
                            else -> "application/octet-stream"
                        }
                
                val shareIntent = AndroidIntent(AndroidIntent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(AndroidIntent.EXTRA_STREAM, uri)
                    addFlags(AndroidIntent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                context.startActivity(AndroidIntent.createChooser(shareIntent, "Compartir reporte"))
            } else {
                android.util.Log.e("ReportesScreen", "Archivo no encontrado: $rutaArchivo")
            }
        } catch (e: Exception) {
            android.util.Log.e("ReportesScreen", "Error al compartir archivo", e)
        }
    }
    
    // LaunchedEffect para compartir archivos automáticamente
    LaunchedEffect(uiState.archivoParaCompartir) {
        uiState.archivoParaCompartir?.let { rutaArchivo ->
            compartirArchivo(rutaArchivo)
        }
    }
    
    var mostrarFiltros by remember { mutableStateOf(false) }

    val padding = when (windowSizeClass.widthSizeClass) {
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact -> PaddingValues(16.dp)
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium -> PaddingValues(24.dp)
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> PaddingValues(32.dp)
        else -> PaddingValues(16.dp)
    }

    // Cargar reportes con fechas específicas
    LaunchedEffect(uiState.fechaInicio, uiState.fechaFin) {
        viewModel.generarReporte()
    }
    
    // Manejar compartir archivos cuando se complete la exportación
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { mensaje ->
            if (mensaje.contains("Excel") && mensaje.contains("guardado en:")) {
                val rutaArchivo = mensaje.substringAfter("guardado en: ")
                compartirArchivo(rutaArchivo)
            } else if (mensaje.contains("CSV") && mensaje.contains("guardado en:")) {
                val rutaArchivo = mensaje.substringAfter("guardado en: ")
                compartirArchivo(rutaArchivo)
            }
        }
    }

    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con logo StaffAxis y botón de filtros (ARRIBA DEL TODO)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo y título StaffAxis
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Logo StaffAxis
                Image(
                    painter = painterResource(id = R.drawable.logo_staffaxis),
                    contentDescription = "Logo StaffAxis",
                    modifier = Modifier.size(40.dp)
                )
                
                Column {
                    Text(
                        text = "StaffAxis",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Reportes y Exportación",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFB0B0B0)
                    )
                }
            }
            
            IconButton(
                onClick = { mostrarFiltros = !mostrarFiltros }
            ) {
                Icon(
                    imageVector = if (mostrarFiltros) Icons.Default.FilterListOff else Icons.Default.FilterList,
                    contentDescription = if (mostrarFiltros) "Ocultar filtros" else "Mostrar filtros",
                    tint = Color.White
                )
            }
        }

        // SALUDO AL ENCARGADO (después del header)
        Text(
            text = "Hola ${uiState.nombreEncargado}",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        
        // Mostrar sector actual
        Text(
            text = "Sector: ${uiState.sectorActual}",
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF26C6DA),  // Turquesa
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Card de estadísticas del período con gradiente
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF5E35B1),  // Púrpura arriba
                                Color(0xFF26C6DA)   // Turquesa abajo
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Estadísticas del Período",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Grid 2x3 de estadísticas REALES
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem(
                                value = "${uiState.estadisticas.totalRegistros}",
                                label = "Total Registros",
                                icon = Icons.Default.Assessment
                            )
                            StatItem(
                                value = "${uiState.estadisticas.empleadosUnicos}",
                                label = "Empleados",
                                icon = Icons.Default.People
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem(
                                value = "${uiState.empleadosDelSector.filter { it.activo }.size}",
                                label = "Activos",
                                icon = Icons.Default.Schedule
                            )
                            StatItem(
                                value = "${uiState.empleadosDelSector.size}",
                                label = "Totales",
                                icon = Icons.Default.List
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem(
                                value = String.format("%.1f", uiState.estadisticas.totalHorasTrabajadas),
                                label = "Horas Totales",
                                icon = Icons.Default.AccessTime
                            )
                            StatItem(
                                value = String.format("%.1f", uiState.estadisticas.promedioHorasPorDia),
                                label = "Promedio/Día",
                                icon = Icons.Default.BarChart
                            )
                        }
                    }
                }
            }
        }
        
        // Filtros de fecha
        if (mostrarFiltros) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Rango de fechas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var mostrarDatePickerInicio by remember { mutableStateOf(false) }
                        
                        OutlinedTextField(
                            value = uiState.fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            onValueChange = { },
                            label = { Text("Fecha inicio") },
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { mostrarDatePickerInicio = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                                }
                            }
                        )
                        
                        // DatePicker para fecha inicio
                        if (mostrarDatePickerInicio) {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = uiState.fechaInicio.toEpochDay() * 86400000L
                            )
                            
                            DatePickerDialog(
                                onDismissRequest = { mostrarDatePickerInicio = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            datePickerState.selectedDateMillis?.let { millis ->
                                                val selectedDate = LocalDate.ofEpochDay(millis / 86400000L)
                                                viewModel.actualizarFechas(selectedDate, uiState.fechaFin)
                                            }
                                            mostrarDatePickerInicio = false
                                        }
                                    ) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { mostrarDatePickerInicio = false }) {
                                        Text("Cancelar")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }
                        
                        var mostrarDatePickerFin by remember { mutableStateOf(false) }
                        
                        OutlinedTextField(
                            value = uiState.fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            onValueChange = { },
                            label = { Text("Fecha fin") },
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { mostrarDatePickerFin = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                                }
                            }
                        )
                        
                        // DatePicker para fecha fin
                        if (mostrarDatePickerFin) {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = uiState.fechaFin.toEpochDay() * 86400000L
                            )
                            
                            DatePickerDialog(
                                onDismissRequest = { mostrarDatePickerFin = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            datePickerState.selectedDateMillis?.let { millis ->
                                                val selectedDate = LocalDate.ofEpochDay(millis / 86400000L)
                                                viewModel.actualizarFechas(uiState.fechaInicio, selectedDate)
                                            }
                                            mostrarDatePickerFin = false
                                        }
                                    ) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { mostrarDatePickerFin = false }) {
                                        Text("Cancelar")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                viewModel.actualizarFechas(
                                    LocalDate.now().withDayOfMonth(1),
                                    LocalDate.now()
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Mes actual")
                        }
                        
                        OutlinedButton(
                            onClick = { 
                                viewModel.actualizarFechas(
                                    LocalDate.now().minusMonths(1).withDayOfMonth(1),
                                    LocalDate.now().minusMonths(1).withDayOfMonth(
                                        LocalDate.now().minusMonths(1).lengthOfMonth()
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Mes anterior")
                        }
                    }
                }
            }
        }
        
        // Botones de exportación
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Exportar Reportes",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón Excel
                    Button(
                        onClick = { 
                            android.util.Log.d("ReportesScreen", "Click en botón Excel")
                            viewModel.exportarAExcel()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !uiState.exportandoExcel
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF5E35B1),
                                            Color(0xFF26C6DA)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.exportandoExcel) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.TableChart,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Excel",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    
                    // Botón CSV
                    Button(
                        onClick = { 
                            android.util.Log.d("ReportesScreen", "Click en botón CSV")
                            viewModel.exportarACSV()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !uiState.exportandoCSV
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF5E35B1),
                                            Color(0xFF26C6DA)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.exportandoCSV) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.GridOn,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "CSV",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        
        // Mensajes de estado
        uiState.mensaje?.let { mensaje ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.error != null) 
                        MaterialTheme.colorScheme.errorContainer 
                    else 
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = mensaje,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (uiState.error != null) 
                        MaterialTheme.colorScheme.onErrorContainer 
                    else 
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        uiState.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatItem(value: String, label: String, icon: ImageVector) {
    Column(
        modifier = Modifier.width(150.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFFE0E0E0),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
