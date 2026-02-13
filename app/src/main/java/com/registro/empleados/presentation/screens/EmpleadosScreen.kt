package com.registro.empleados.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.registro.empleados.R
import com.registro.empleados.presentation.components.EmployeeCard
import com.registro.empleados.presentation.viewmodel.EmpleadosViewModel

/**
 * Pantalla de gestión de empleados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadosScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: EmpleadosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // RECARGAR cuando la pantalla se vuelve visible
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                android.util.Log.d("EmpleadosScreen", "🔄 Pantalla resumida - FORZANDO recarga de datos")
                viewModel.forzarRecargaColores()
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // LIMPIAR formulario cuando se monta la pantalla
    LaunchedEffect(Unit) {
        android.util.Log.d("EmpleadosScreen", "🔄 Pantalla montada - limpiando formulario")
        viewModel.limpiarFormulario()
    }

    val padding = when (windowSizeClass.widthSizeClass) {
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact -> PaddingValues(16.dp)
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium -> PaddingValues(24.dp)
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> PaddingValues(32.dp)
        else -> PaddingValues(16.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // Header con logo y título StaffAxis
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
                    text = "Carga de Horas",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFB0B0B0)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Barra de búsqueda con estilo glassmorphism
        OutlinedTextField(
            value = viewModel.searchQuery.value,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Buscar empleado", color = Color(0xFFB0B0B0)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF26C6DA)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x33FFFFFF),  // Blanco semi-transparente
                unfocusedContainerColor = Color(0x22FFFFFF),
                focusedIndicatorColor = Color(0xFF26C6DA),
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Header "Empleados (14)" con nuevo estilo
        Text(
            text = "Empleados (${uiState.empleados.size})",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFB0B0B0),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Formulario de nuevo empleado
        if (uiState.mostrarFormularioNuevo) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Nuevo Empleado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = uiState.formularioNuevoEmpleado.legajo ?: "",
                        onValueChange = { viewModel.onFormFieldChanged(legajo = it) },
                        label = { Text("DNI") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = uiState.formularioNuevoEmpleado.nombreCompleto,
                        onValueChange = { viewModel.onFormFieldChanged(nombreCompleto = it) },
                        label = { Text("Nombre Completo") },
                        placeholder = { Text("Ej: JUAREZ JULIO") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = uiState.formularioNuevoEmpleado.sector,
                        onValueChange = { viewModel.onFormFieldChanged(sector = it) },
                        label = { Text("Sector") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.toggleFormularioNuevoEmpleado() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                        
                        Button(
                            onClick = { viewModel.guardarNuevoEmpleado() },
                            modifier = Modifier.weight(1f),
                            enabled = uiState.formularioNuevoEmpleado.estaCompleto && !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Lista de empleados con nuevo diseño
        LazyColumn(
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = uiState.empleados,
                key = { empleado -> empleado.id }  // Usar ID único (siempre presente y único)
            ) { empleado ->
                val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
                val tieneHoras = uiState.empleadosConHorasHoy.contains(legajoKey)
                val estaAusente = uiState.empleadosAusentesHoy.contains(legajoKey)
                
                android.util.Log.d("EmpleadosScreen", "🎨 Renderizando: ${empleado.nombreCompleto}")
                android.util.Log.d("EmpleadosScreen", "  LegajoKey: $legajoKey")
                android.util.Log.d("EmpleadosScreen", "  Set tiene este key: ${uiState.empleadosConHorasHoy.contains(legajoKey)}")
                android.util.Log.d("EmpleadosScreen", "  Tiene horas: $tieneHoras")
                
                EmpleadoCard(
                    empleado = empleado,
                    tieneHorasCargadasHoy = tieneHoras,
                    estaAusenteHoy = estaAusente,
                    onInfoClick = { 
                        // Abrir diálogo de carga de horas
                    },
                    onEditClick = {
                        // Abrir diálogo de edición
                    }
                )
            }
        }
        
        // Mensajes de estado
        uiState.mensaje?.let { mensaje ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = mensaje,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
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
        
        // FAB (Botón flotante de agregar)
        FloatingActionButton(
            onClick = { viewModel.toggleFormularioNuevoEmpleado() },
            containerColor = Color.Transparent,
            modifier = Modifier
                .size(64.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE91E63),  // Rosa centro
                            Color(0xFF9C27B0)   // Púrpura borde
                        )
                    ),
                    shape = CircleShape
                )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Agregar empleado",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun EmpleadoCard(
    empleado: com.registro.empleados.domain.model.Empleado,
    tieneHorasCargadasHoy: Boolean,
    estaAusenteHoy: Boolean,  // AGREGAR PARÁMETRO DE AUSENCIA
    onInfoClick: () -> Unit,
    onEditClick: () -> Unit
) {
    // Logs ruidosos removidos
    // Determinar colores según estado
    val cardColor = when {
        estaAusenteHoy -> listOf(
            Color(0xFFFF5252),  // Rojo ausencia
            Color(0xFFD32F2F)   // Rojo más oscuro
        )
        tieneHorasCargadasHoy -> listOf(
            Color(0xFF2E7D32),  // Verde oscuro (consistente con tema Material)
            Color(0xFF66BB6A)   // Verde medio
        )
        else -> listOf(
            Color(0xFF2A223C),  // Más negro/neutral dentro de la paleta morada
            Color(0xFF1E1E2E)   // Base oscura existente
        )
    }
    
    // Colores de texto (contraste)
    val textColor = when {
        estaAusenteHoy -> Color.White
        tieneHorasCargadasHoy -> Color.White
        else -> Color.White
    }
    
    // Colores de íconos según estado
    val clockIconColor = when {
        estaAusenteHoy -> Color.White
        tieneHorasCargadasHoy -> Color.White
        else -> Color(0xFF42EEDB)  // Primera imagen: ícono de reloj turquesa
    }
    
    val editIconColor = when {
        estaAusenteHoy -> Color.White
        tieneHorasCargadasHoy -> Color.White
        else -> Color(0xFF9C27B0)  // Primera imagen: ícono de lápiz púrpura
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors = cardColor),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = empleado.nombreCompleto.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor,  // Color dinámico
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // MOSTRAR DNI O "SIN DATOS"
                    Text(
                        text = if (empleado.legajo != null) {
                            "DNI: ${empleado.legajo}"
                        } else {
                            "DNI: Sin datos"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.9f),
                        fontStyle = if (empleado.legajo == null) FontStyle.Italic else FontStyle.Normal
                    )
                    Text(
                        text = "Ingreso: ${empleado.fechaIngreso}",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.8f)
                    )
                    
                    // MOSTRAR ESTADO DE AUSENCIA
                    if (estaAusenteHoy) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🚫 AUSENTE",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Botón de info (reloj)
                IconButton(
                    onClick = onInfoClick,
                    enabled = !estaAusenteHoy,  // DESHABILITAR SI ESTÁ AUSENTE
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (tieneHorasCargadasHoy) Color(0x22000000) else Color.Transparent,  // Fondo solo para estado con horas
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Schedule,  // Icono de reloj
                        contentDescription = "Ver horas",
                        tint = if (estaAusenteHoy) Color(0x88FFFFFF) else clockIconColor  // Color deshabilitado si ausente
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Botón de editar
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (tieneHorasCargadasHoy) Color(0x22000000) else Color.Transparent,  // Fondo solo para estado con horas
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = editIconColor  // Color específico del lápiz
                    )
                }
            }
        }
    }
}