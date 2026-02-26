package com.registro.empleados.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.hilt.navigation.compose.hiltViewModel
import com.registro.empleados.presentation.viewmodel.AusenciasViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

@Composable
fun RegistroAusenciasScreen(
    viewModel: AusenciasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Solo cargar datos cuando la pantalla se compone por primera vez
    LaunchedEffect(Unit) {
        // No hacer nada aquí, el ViewModel ya maneja la carga inicial
        // Esto evita recargas múltiples al cambiar de pestaña
    }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            ),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.mostrarDialogoAgregarAusencia() },
                containerColor = Color(0xFFFF5252),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar ausencia",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .padding(bottom = 80.dp)  // Espacio para FAB + bottom nav
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            Text(
                text = "Registro de Ausencias",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navegación de período
            PeriodoNavigation(
                periodo = uiState.periodoActual,
                onAnterior = { viewModel.cambiarMes(-1) },
                onSiguiente = { viewModel.cambiarMes(1) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Leyenda
            CalendarLegend()
            Spacer(modifier = Modifier.height(16.dp))
            
            // CALENDARIO CON PUNTOS ROJOS
                if (uiState.isLoading) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E1E2E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF42EEDB)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Cargando calendario...",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else if (uiState.diasLaborales.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E1E2E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.EventBusy,
                                    contentDescription = null,
                                    tint = Color(0xFF888888),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No hay datos del calendario",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Verifique la configuración del período",
                                    color = Color(0xFF888888),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else {
                    CustomCalendarGridConAusencias(
                        diasLaborales = uiState.diasLaborales,
                        fechasConAusencias = uiState.fechasConAusencias,
                        onDayClick = { fecha -> viewModel.onFechaClick(fecha) }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            
            // Mostrar error si existe
            if (uiState.error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD32F2F)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Error:",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.error!!.take(100) + if (uiState.error!!.length > 100) "..." else "",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            
            // RESUMEN DEL PERÍODO (sin fondo gris ni elevación)
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                        Text(
                            text = "Resumen del Período",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Período: ${uiState.periodoActual.fechaInicio} - ${uiState.periodoActual.fechaFin}",
                            color = Color(0xFFB0B0B0),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Días con ausentes: ${uiState.fechasConAusencias.size}",
                            color = Color(0xFFFF5252),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total ausencias registradas: ${uiState.ausencias.size}",
                            color = Color(0xFFB0B0B0),
                            style = MaterialTheme.typography.bodyMedium
                        )
                }
        }
    }
    
    // DIÁLOGO PARA AGREGAR AUSENCIA
    if (uiState.mostrarDialogoAgregar) {
        DialogoAgregarAusencia(
            viewModel = viewModel,
            onDismiss = { viewModel.cerrarDialogoAgregarAusencia() },
            onAusenciaCreada = { 
                viewModel.cerrarDialogoAgregarAusencia()
                viewModel.cargarCalendario()
            }
        )
    }
    
    // DIÁLOGO PARA EDITAR AUSENCIA
    if (uiState.mostrarDialogoEditar && uiState.ausenciaParaEditar != null) {
        DialogoEditarAusencia(
            ausencia = uiState.ausenciaParaEditar!!,
            onDismiss = { viewModel.cerrarDialogoEditar() },
            onGuardar = { fechaInicio, fechaFin, motivo ->
                viewModel.actualizarAusencia(
                    ausenciaId = uiState.ausenciaParaEditar!!.id,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    motivo = motivo
                )
            }
        )
    }
    
    // DIÁLOGO DETALLES DE FECHA
    if (uiState.mostrarDetallesFecha && uiState.fechaSeleccionada != null) {
        DialogoDetallesFecha(
            fecha = uiState.fechaSeleccionada!!,
            ausentes = uiState.ausentesEnFechaSeleccionada,
            onDismiss = { viewModel.cerrarDetallesFecha() },
            onEditar = { ausencia ->
                viewModel.abrirDialogoEditarAusencia(ausencia)
            },
            onEliminar = { ausencia ->
                viewModel.solicitarEliminarAusencia(ausencia)
            }
        )
    }
    
    // DIÁLOGO DE CONFIRMACIÓN PARA ELIMINAR
    if (uiState.mostrarConfirmacionEliminar && uiState.ausenciaParaEliminar != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelarEliminarAusencia() },
            title = {
                Text(
                    "Confirmar Eliminación",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "¿Está seguro que desea eliminar esta ausencia?",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0x22FF5252)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = uiState.ausenciaParaEliminar!!.nombreEmpleado,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "DNI: ${uiState.ausenciaParaEliminar!!.legajoEmpleado}",
                                color = Color(0xFFB0B0B0),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Período: ${uiState.ausenciaParaEliminar!!.fechaInicio} - ${uiState.ausenciaParaEliminar!!.fechaFin}",
                                color = Color(0xFFFF5252),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmarEliminarAusencia() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelarEliminarAusencia() }) {
                    Text("Cancelar")
                }
            },
            containerColor = Color(0xFF1E1E2E)
        )
    } 
}

@Composable
fun PeriodoNavigation(
    periodo: com.registro.empleados.domain.model.PeriodoLaboral,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A3E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onAnterior,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFF9C27B0),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Mes anterior",
                    tint = Color.White
                )
            }
            
            Text(
                text = "${periodo.fechaInicio.format(DateTimeFormatter.ofPattern("MMM yyyy")).replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = onSiguiente,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFF9C27B0),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Mes siguiente",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun CalendarLegend() {
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            Text(
                text = "Leyenda del Calendario",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(
                    color = Color(0x1126C6DA),
                    label = "Fin de Semana"
                )
                LegendItem(
                    color = Color(0x22FF5252),
                    label = "Días con Ausentes"
                )
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB0B0B0),
            fontSize = 11.sp
        )
    }
}

@Composable
fun CustomCalendarGridConAusencias(
    diasLaborales: List<com.registro.empleados.domain.model.DiaLaboral>,
    fechasConAusencias: Set<LocalDate>,
    onDayClick: (LocalDate) -> Unit
) {
    // Crear un calendario completo del mes
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Encabezados de días de la semana (empezando por Domingo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb").forEach { dia ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dia,
                            color = Color(0xFF888888),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Crear calendario completo del mes
            if (diasLaborales.isNotEmpty()) {
                // Usar el primer día para determinar el mes a mostrar
                val primerDia = diasLaborales.first().fecha
                val año = primerDia.year
                val mes = primerDia.monthValue
                
                // Crear mapa de días laborales por fecha para acceso rápido
                val diasPorFecha = diasLaborales.associateBy { it.fecha }
                
                // Generar todas las fechas del mes actual solamente
                val todasLasFechas = mutableListOf<LocalDate>()
                var fechaActual = LocalDate.of(año, mes, 1) // Primer día del mes
                val ultimoDiaDelMes = fechaActual.withDayOfMonth(fechaActual.lengthOfMonth())
                
                while (!fechaActual.isAfter(ultimoDiaDelMes)) {
                    todasLasFechas.add(fechaActual)
                    fechaActual = fechaActual.plusDays(1)
                }
                
                // Crear semanas del calendario
                val semanas = mutableListOf<MutableList<LocalDate?>>()
                var semanaActual = mutableListOf<LocalDate?>()
                
                // Agregar espacios vacíos al inicio para alinear con los días de la semana
                val primerDiaDelMes = todasLasFechas.first()
                // En Java DayOfWeek: 1=Lunes, 2=Martes, ..., 7=Domingo
                // Para calendario que empieza por Domingo: Domingo=0, Lunes=1, ..., Sábado=6
                val diasVaciosInicio = if (primerDiaDelMes.dayOfWeek.value == 7) 0 else primerDiaDelMes.dayOfWeek.value
                
                repeat(diasVaciosInicio) {
                    semanaActual.add(null)
                }
                
                // Agregar todos los días del mes
                todasLasFechas.forEach { fecha ->
                    semanaActual.add(fecha)
                    
                    // Si completamos una semana (7 días), agregar a la lista de semanas
                    if (semanaActual.size == 7) {
                        semanas.add(semanaActual)
                        semanaActual = mutableListOf()
                    }
                }
                
                // Completar la última semana si es necesario
                if (semanaActual.isNotEmpty()) {
                    while (semanaActual.size < 7) {
                        semanaActual.add(null)
                    }
                    semanas.add(semanaActual)
                }
                
                // Renderizar cada semana
                semanas.forEach { semana ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        semana.forEach { fecha ->
                            if (fecha != null) {
                                val diaLaboral = diasPorFecha[fecha]
                                val tieneAusentes = fechasConAusencias.contains(fecha)
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .background(
                                            color = when {
                                                tieneAusentes -> Color(0x22FF5252)  // Fondo rojo tenue
                                                diaLaboral?.esLaboral == false -> Color(0x1126C6DA) // Fin de semana
                                                else -> Color(0x11FFFFFF) // Día normal
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onDayClick(fecha) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = fecha.dayOfMonth.toString(),
                                            color = when {
                                                tieneAusentes -> Color(0xFFFF5252)  // Número rojo
                                                diaLaboral?.esLaboral == false -> Color(0xFF26C6DA) // Fin de semana
                                                else -> Color.White // Día normal
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (tieneAusentes) FontWeight.Bold else FontWeight.Normal
                                        )
                                        
                                        // PUNTO ROJO si tiene ausentes
                                        if (tieneAusentes) {
                                            Spacer(modifier = Modifier.height(1.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .background(
                                                        color = Color(0xFFFF5252),
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Espacio vacío
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                }
            } else {
                // Mostrar mensaje si no hay días
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay días disponibles",
                        color = Color(0xFF888888),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoAgregarAusencia(
    viewModel: AusenciasViewModel,
    onDismiss: () -> Unit,
    onAusenciaCreada: () -> Unit
) {
    var legajoSeleccionado by remember { mutableStateOf("") }
    var empleadoSeleccionado by remember { mutableStateOf<com.registro.empleados.domain.model.Empleado?>(null) }
    var fechaInicio by remember { mutableStateOf(LocalDate.now()) }
    var fechaFin by remember { mutableStateOf(LocalDate.now()) }
    val motivoFijo = "Accidente laboral"
    var expandido by remember { mutableStateOf(false) }
    var mostrarDatePickerInicio by remember { mutableStateOf(false) }
    var mostrarDatePickerFin by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    val empleadosDelSector = uiState.empleadosDelSector
    
    // Debug: Log empleados
    LaunchedEffect(empleadosDelSector) {
        android.util.Log.d("DialogoAusencia", "Empleados del sector: ${empleadosDelSector.size}")
        empleadosDelSector.forEach { empleado ->
            android.util.Log.d("DialogoAusencia", "Empleado: ${empleado.nombreCompleto} (${empleado.legajo})")
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Registrar Ausencia",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp) // Limitar altura máxima
            ) {
                // Selector de empleado
                Text(
                    "Empleado",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (empleadosDelSector.isEmpty()) {
                    // Mostrar mensaje cuando no hay empleados
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A3E)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.PersonOff,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "No hay empleados disponibles",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Verifique que haya empleados en el sector seleccionado",
                                    color = Color(0xFFB0B0B0),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = expandido,
                        onExpandedChange = { expandido = it }
                    ) {
                        OutlinedTextField(
                            value = empleadoSeleccionado?.nombreCompleto ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Seleccione un empleado (${empleadosDelSector.size} disponibles)") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expandido,
                            onDismissRequest = { expandido = false }
                        ) {
                            empleadosDelSector.forEach { empleado ->
                                DropdownMenuItem(
                                    text = { Text("${empleado.nombreCompleto} (${empleado.legajo ?: "Sin DNI"})") },
                                    onClick = {
                                        empleadoSeleccionado = empleado
                                        legajoSeleccionado = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
                                        expandido = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Fecha inicio
                Text("Fecha Inicio", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { mostrarDatePickerInicio = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2A3E)
                    )
                ) {
                    Text(
                        text = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Fecha fin
                Text("Fecha Fin", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { mostrarDatePickerFin = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2A3E)
                    )
                ) {
                    Text(
                        text = fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Motivo fijo: Accidente laboral
                OutlinedTextField(
                    value = motivoFijo,
                    onValueChange = { },
                    label = { Text("Motivo") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.White,
                        disabledBorderColor = Color(0xFF555555),
                        disabledContainerColor = Color(0xFF2A2A3E)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (empleadoSeleccionado != null) {
                        viewModel.crearAusencia(
                            legajo = legajoSeleccionado,
                            nombreEmpleado = empleadoSeleccionado!!.nombreCompleto,
                            fechaInicio = fechaInicio,
                            fechaFin = fechaFin,
                            motivo = motivoFijo
                        )
                        onAusenciaCreada()
                    }
                },
                enabled = empleadoSeleccionado != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5252)
                )
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = Color(0xFF1E1E2E)
    )
    
    // DatePicker para fecha inicio
    if (mostrarDatePickerInicio) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaInicio.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { mostrarDatePickerInicio = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            fechaInicio = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                        }
                        mostrarDatePickerInicio = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    )
                ) {
                    Text("Confirmar")
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
    
    // DatePicker para fecha fin
    if (mostrarDatePickerFin) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaFin.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { mostrarDatePickerFin = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            fechaFin = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                        }
                        mostrarDatePickerFin = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    )
                ) {
                    Text("Confirmar")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoEditarAusencia(
    ausencia: com.registro.empleados.domain.model.Ausencia,
    onDismiss: () -> Unit,
    onGuardar: (LocalDate, LocalDate, String?) -> Unit
) {
    var fechaInicio by remember { mutableStateOf(ausencia.fechaInicio) }
    var fechaFin by remember { mutableStateOf(ausencia.fechaFin) }
    var mostrarDatePickerInicio by remember { mutableStateOf(false) }
    var mostrarDatePickerFin by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Editar Ausencia",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Empleado (no editable)
                Text(
                    "Empleado",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A3E)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = ausencia.nombreEmpleado,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "DNI: ${ausencia.legajoEmpleado}",
                            color = Color(0xFFB0B0B0),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Fecha inicio
                Text("Fecha Inicio", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { mostrarDatePickerInicio = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2A3E)
                    )
                ) {
                    Text(
                        text = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Fecha fin
                Text("Fecha Fin", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { mostrarDatePickerFin = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2A3E)
                    )
                ) {
                    Text(
                        text = fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Motivo fijo: Accidente laboral
                OutlinedTextField(
                    value = "Accidente laboral",
                    onValueChange = { },
                    label = { Text("Motivo") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.White,
                        disabledBorderColor = Color(0xFF555555),
                        disabledContainerColor = Color(0xFF2A2A3E)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(fechaInicio, fechaFin, "Accidente laboral")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF26C6DA)
                )
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = Color(0xFF1E1E2E)
    )
    
    // DatePicker para fecha inicio
    if (mostrarDatePickerInicio) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaInicio.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { mostrarDatePickerInicio = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            fechaInicio = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                        }
                        mostrarDatePickerInicio = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF26C6DA)
                    )
                ) {
                    Text("Confirmar")
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
    
    // DatePicker para fecha fin
    if (mostrarDatePickerFin) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaFin.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { mostrarDatePickerFin = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            fechaFin = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                        }
                        mostrarDatePickerFin = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF26C6DA)
                    )
                ) {
                    Text("Confirmar")
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

@Composable
fun DialogoDetallesFecha(
    fecha: LocalDate,
    ausentes: List<com.registro.empleados.domain.model.Ausencia>,
    onDismiss: () -> Unit,
    onEditar: (com.registro.empleados.domain.model.Ausencia) -> Unit = {},
    onEliminar: (com.registro.empleados.domain.model.Ausencia) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Ausentes - $fecha",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                if (ausentes.isEmpty()) {
                    Text(
                        "No hay ausentes en esta fecha",
                        color = Color(0xFFB0B0B0)
                    )
                } else {
                    ausentes.forEach { ausencia ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0x22FF5252)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ausencia.nombreEmpleado,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "DNI: ${ausencia.legajoEmpleado}",
                                    color = Color(0xFFB0B0B0),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Período: ${ausencia.fechaInicio} - ${ausencia.fechaFin}",
                                    color = Color(0xFFFF5252),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                    ausencia.motivo?.let { motivoText ->
                                    Text(
                                            text = "Motivo: $motivoText",
                                        color = Color(0xFFB0B0B0),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                                
                                Row {
                                    IconButton(
                                        onClick = { onEditar(ausencia) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = Color(0xFF26C6DA),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onEliminar(ausencia) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color(0xFFFF5252),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        containerColor = Color(0xFF1E1E2E)
    )
}
