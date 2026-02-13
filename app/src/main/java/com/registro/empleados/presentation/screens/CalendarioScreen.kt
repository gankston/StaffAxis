package com.registro.empleados.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import com.registro.empleados.R
import com.registro.empleados.presentation.viewmodel.CalendarioViewModel

/**
 * Pantalla del calendario de asistencia personalizado (período 26-25).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: CalendarioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        android.util.Log.d("CalendarioScreen", "Screen montada")
    }
    
    // FIXED: Proper composable structure without early returns
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Cargando calendario...")
                }
            }
        }
        
        uiState.error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Calendario de Asistencia",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "❌ ${uiState.error}",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.recargarCalendario() }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
        
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 80.dp),  // Espacio para Bottom Navigation
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header con logo StaffAxis
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            text = "Calendario de Asistencia",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFB0B0B0)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                    // Card de información del período con gradiente
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF5E35B1),  // Púrpura
                                            Color(0xFF26C6DA)   // Turquesa
                                        )
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Período: ${uiState.periodoActual.fechaInicio} - ${uiState.periodoActual.fechaFin}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Días cargados: ${uiState.diasLaborales.size}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFE0E0E0)
                                    )
                                }
                            }
                        }
                    }

                    // Botón "Recargar" con gradiente
                    Button(
                        onClick = { viewModel.recargarCalendario() },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(48.dp)
                            .align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFE91E63),  // Rosa
                                            Color(0xFF9C27B0)   // Púrpura
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Recargar", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                
                // AGREGAR GRILLA DEL CALENDARIO
                if (uiState.diasLaborales.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Calendario del Período",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            // Grilla de días
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(7), // 7 columnas para los días de la semana
                                modifier = Modifier.height(400.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Encabezados de días de la semana
                                items(7) { index ->
                                    val diasSemana = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
                                    Card(
                                        modifier = Modifier.size(40.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = diasSemana[index],
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                
                                    // Días del período
                                    items(uiState.diasLaborales.size) { index ->
                                        val dia = uiState.diasLaborales[index]
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = Color(0x22FFFFFF),  // Fondo semi-transparente
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = dia.fecha.dayOfMonth.toString(),
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                // Indicador (punto de color según estado)
                                                if (dia.esLaboral) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .background(
                                                                color = when {
                                                                    dia.tipoDia.name == "FERIADO" -> Color(0xFFE91E63)  // Rosa
                                                                    dia.tipoDia.name == "FIN_DE_SEMANA" -> Color(0xFF26C6DA)  // Turquesa
                                                                    else -> Color(0xFF9C27B0)  // Púrpura
                                                                },
                                                                shape = CircleShape
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                            }
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No hay datos del calendario para mostrar")
                        }
                    }
                }
            }
        }
    }
}