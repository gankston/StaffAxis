package com.registro.empleados.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.presentation.viewmodel.DashboardUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * Dashboard responsive que se adapta a diferentes tamaños de pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsiveDashboard(
    windowSizeClass: WindowSizeClass,
    uiState: DashboardUiState,
    onBusquedaChanged: (String) -> Unit,
    onAbrirDialogoRegistroHoras: (Empleado) -> Unit = {},
    onFechaChanged: (LocalDate) -> Unit,
    onHorasChanged: (Int) -> Unit,
    onObservacionesChanged: (String) -> Unit,
    onGuardarRegistro: () -> Unit,
    onAgregarEmpleado: () -> Unit = {},
    onNuevoEmpleadoLegajoChanged: (String) -> Unit = {},
    onNuevoEmpleadoNombreChanged: (String) -> Unit = {},
    onNuevoEmpleadoApellidoChanged: (String) -> Unit = {},
    onCrearNuevoEmpleado: () -> Unit = {},
    onCerrarDialogoNuevoEmpleado: () -> Unit = {},
    onConfirmarTraspasoEmpleado: () -> Unit = {},
    onCancelarTraspasoEmpleado: () -> Unit = {},
    onCerrarDialogoRegistroHoras: () -> Unit = {},
    onEditarEmpleado: (Empleado) -> Unit = {},
    onEditarEmpleadoLegajoChanged: (String) -> Unit = {},
    onEditarEmpleadoNombreChanged: (String) -> Unit = {},
    onEditarEmpleadoSectorChanged: (String) -> Unit = {},
    onGuardarEmpleadoEditado: () -> Unit = {},
    onCerrarDialogoEditarEmpleado: () -> Unit = {},
    onEditarRegistroHoras: (com.registro.empleados.domain.model.RegistroAsistencia) -> Unit = {},
    onCerrarEdicionRegistroHoras: () -> Unit = {},
    onHorasEdicionChanged: (Int) -> Unit = {},
    onGuardarEdicionRegistroHoras: () -> Unit = {},
    onAbrirDialogoConfirmarEliminar: (Empleado) -> Unit = {},
    onCerrarDialogoConfirmarEliminar: () -> Unit = {},
    onEliminarEmpleadoConfirmado: () -> Unit = {},
    onCerrarMensajeEliminacion: () -> Unit = {},
    onCerrarMensajeRegistroExitoso: () -> Unit = {},
    onCerrarMensajeRegistroDuplicado: () -> Unit = {},
    onCerrarMensajeEmpleadoCreado: () -> Unit = {},
    onCerrarMensajeEmpleadoEditado: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val padding = remember(windowSizeClass.widthSizeClass) {
        when (windowSizeClass.widthSizeClass) {
            androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact -> PaddingValues(16.dp)
            androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium -> PaddingValues(24.dp)
            androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> PaddingValues(32.dp)
            else -> PaddingValues(16.dp)
        }
    }
    
    val spacing = remember(windowSizeClass.widthSizeClass) {
        when (windowSizeClass.widthSizeClass) {
            androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact -> 8
            androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium -> 12
            androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> 16
            else -> 8
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                bottom = 120.dp  // Espacio suficiente para Bottom Navigation + FAB
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.dp)
        ) {
            item {
                Text(
                    text = "Carga de Horas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Una sola carga por día. Use 'Editar horas' en la ficha del empleado para corregir.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(spacing.dp))
                
                // Campo de búsqueda global
                OutlinedTextField(
                    value = uiState.busqueda,
                    onValueChange = onBusquedaChanged,
                    label = { Text("Buscar empleado") },
                    placeholder = { Text("Buscar por DNI, nombre o apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            }
            
            // Lista de empleados
            if (uiState.empleadosFiltrados.isNotEmpty()) {
                item {
                    Text(
                        text = "Empleados (${uiState.empleadosFiltrados.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = spacing.dp)
                    )
                }
                
                items(
                    items = uiState.empleadosFiltrados,
                    key = { it.id } // Usar ID único (siempre presente y único)
                ) { empleado ->
                    // Usar una clave consistente para comparaciones cuando el DNI es nulo
                    val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"

                    EmpleadoCard(
                        empleado = empleado,
                        tieneHorasCargadasHoy = uiState.empleadosConHorasHoy.contains(legajoKey),
                        estaAusenteHoy = uiState.empleadosAusentesHoy.contains(legajoKey),
                        // Evitar que múltiples empleados con DNI nulo queden "seleccionados"
                        isSelected = uiState.empleadoEncontrado?.id == empleado.id,
                        onEditClick = { onEditarEmpleado(empleado) },
                        onRegistrarHorasClick = { onAbrirDialogoRegistroHoras(empleado) }
                    )
                }
            } else if (uiState.busqueda.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "No se encontraron empleados con \"${uiState.busqueda}\"",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Estado actual del empleado encontrado
            uiState.registroHoy?.let { registro ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Estado de Hoy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Fecha: ${registro.fecha}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Horas trabajadas: ${registro.horasTrabajadas}h",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            registro.observaciones?.let { obs ->
                                Text(
                                    text = "Observaciones: $obs",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Botón flotante para agregar empleado
        FloatingActionButton(
            onClick = onAgregarEmpleado,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar empleado")
        }
        
        // --- DIÁLOGOS ---
        
        if (uiState.mostrarDialogoNuevoEmpleado) {
            NuevoEmpleadoDialog(
                uiState = uiState,
                onDismiss = onCerrarDialogoNuevoEmpleado,
                onLegajoChanged = onNuevoEmpleadoLegajoChanged,
                onNombreChanged = onNuevoEmpleadoNombreChanged,
                onApellidoChanged = onNuevoEmpleadoApellidoChanged,
                onConfirm = onCrearNuevoEmpleado
            )
        }
        if (uiState.empleadoExistenteParaTraspaso != null) {
            AlertDialog(
                onDismissRequest = onCancelarTraspasoEmpleado,
                title = { Text("Empleado ya registrado") },
                text = {
                    Column {
                        val emp = uiState.empleadoExistenteParaTraspaso!!
                        Text("El empleado ${emp.nombreCompleto} (DNI: ${emp.legajo}) ya está dado de alta en el sector ${emp.sector}.")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("¿Desea agregarlo a su sector (${uiState.nuevoEmpleadoSector})?", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Aviso: No se eliminará del otro sector, se traspasará a su sector.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                confirmButton = { Button(onClick = onConfirmarTraspasoEmpleado) { Text("Sí, traspasar") } },
                dismissButton = { TextButton(onClick = onCancelarTraspasoEmpleado) { Text("Cancelar") } }
            )
        }
        
        if (uiState.mostrarDialogoRegistroHoras && uiState.empleadoEncontrado != null) {
            RegistroHorasDialog(
                uiState = uiState,
                onDismiss = onCerrarDialogoRegistroHoras,
                onFechaChanged = onFechaChanged,
                onHorasChanged = onHorasChanged,
                onObservacionesChanged = onObservacionesChanged,
                onConfirm = onGuardarRegistro
            )
        }
        
        if (uiState.mostrarDialogoEditarEmpleado && uiState.empleadoParaEditar != null) {
            EditarEmpleadoDialog(
                uiState = uiState,
                onDismiss = onCerrarDialogoEditarEmpleado,
                onLegajoChanged = onEditarEmpleadoLegajoChanged,
                onNombreChanged = onEditarEmpleadoNombreChanged,
                onSectorChanged = onEditarEmpleadoSectorChanged,
                onConfirm = onGuardarEmpleadoEditado,
                onDelete = {
                    onAbrirDialogoConfirmarEliminar(uiState.empleadoParaEditar)
                },
                onEditarRegistroHoras = onEditarRegistroHoras,
                onCerrarEdicionRegistroHoras = onCerrarEdicionRegistroHoras,
                onHorasEdicionChanged = onHorasEdicionChanged,
                onGuardarEdicionRegistroHoras = onGuardarEdicionRegistroHoras
            )
        }
        
        if (uiState.mostrarDialogoConfirmarEliminar && uiState.empleadoParaEliminar != null) {
            ConfirmarEliminarDialog(
                empleado = uiState.empleadoParaEliminar,
                onDismiss = onCerrarDialogoConfirmarEliminar,
                onConfirm = onEliminarEmpleadoConfirmado
            )
        }
        
        // --- CARTELES FLOTANTES ---
        
        if (uiState.mostrarMensajeEliminacion) {
            ConfirmacionFlotante(
                mensajePrincipal = "✅ Empleado Eliminado",
                mensajeSecundario = "El empleado ha sido eliminado exitosamente.",
                icono = Icons.Default.CheckCircle,
                colorFondo = Color(0xFFD32F2F),
                onDismiss = onCerrarMensajeEliminacion
            )
        }
        
        if (uiState.mostrarMensajeRegistroExitoso) {
            ConfirmacionFlotante(
                mensajePrincipal = "✅ Registro Exitoso",
                mensajeSecundario = uiState.mensajeRegistroExitoso,
                icono = Icons.Default.CheckCircle,
                colorFondo = Color(0xFF1976D2),
                onDismiss = onCerrarMensajeRegistroExitoso
            )
        }

        if (uiState.mostrarMensajeRegistroDuplicado) {
            ConfirmacionFlotante(
                mensajePrincipal = "⚠️ Registro Duplicado",
                mensajeSecundario = uiState.mensajeRegistroDuplicado,
                icono = Icons.Default.Warning,
                colorFondo = Color(0xFFFFA000),
                onDismiss = onCerrarMensajeRegistroDuplicado
            )
        }
        
        if (uiState.mostrarMensajeEmpleadoCreado) {
            ConfirmacionFlotante(
                mensajePrincipal = "✅ Empleado Creado",
                mensajeSecundario = uiState.mensajeEmpleadoCreado,
                icono = Icons.Default.PersonAdd,
                colorFondo = Color(0xFF4CAF50),
                onDismiss = onCerrarMensajeEmpleadoCreado
            )
        }
        
        if (uiState.mostrarMensajeEmpleadoEditado) {
            ConfirmacionFlotante(
                mensajePrincipal = "✅ Empleado Editado",
                mensajeSecundario = uiState.mensajeEmpleadoEditado,
                icono = Icons.Default.Edit,
                colorFondo = Color(0xFF9C27B0),
                onDismiss = onCerrarMensajeEmpleadoEditado
            )
        }
    }
}

// --- DIÁLOGOS EXTRAÍDOS ---

@Composable
private fun NuevoEmpleadoDialog(
    uiState: DashboardUiState,
    onDismiss: () -> Unit,
    onLegajoChanged: (String) -> Unit,
    onNombreChanged: (String) -> Unit,
    onApellidoChanged: (String) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nuevo Empleado") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.nuevoEmpleadoLegajo,
                    onValueChange = onLegajoChanged,
                    label = { Text("DNI *") },
                    placeholder = { Text("Ingrese el DNI") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.nuevoEmpleadoLegajo.isBlank()
                )
                OutlinedTextField(
                    value = uiState.nuevoEmpleadoNombre,
                    onValueChange = onNombreChanged,
                    label = { Text("Nombre *") },
                    isError = uiState.nuevoEmpleadoNombre.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.nuevoEmpleadoApellido,
                    onValueChange = onApellidoChanged,
                    label = { Text("Apellido *") },
                     isError = uiState.nuevoEmpleadoApellido.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.nuevoEmpleadoSector,
                    onValueChange = { }, // No se puede cambiar
                    label = { Text("Sector") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = uiState.nuevoEmpleadoLegajo.isNotBlank() && uiState.nuevoEmpleadoNombre.isNotBlank() && uiState.nuevoEmpleadoApellido.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistroHorasDialog(
    uiState: DashboardUiState,
    onDismiss: () -> Unit,
    onFechaChanged: (LocalDate) -> Unit,
    onHorasChanged: (Int) -> Unit,
    onObservacionesChanged: (String) -> Unit,
    onConfirm: () -> Unit
) {
    val empleado = uiState.empleadoEncontrado ?: return
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Horas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Solo se permite una carga de horas por día. Use 'Editar horas' en la ficha del empleado para corregir en caso de equivocación.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                        Text(empleado.nombreCompleto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("DNI: ${empleado.legajo ?: "Sin datos"}", style = MaterialTheme.typography.bodyMedium)
                        Text("Sector: ${empleado.sector}", style = MaterialTheme.typography.bodySmall)
                        if (empleado.observacion != null && empleado.observacion.isNotBlank()) {
                            Text("Observación: ${empleado.observacion}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                
                var mostrarDatePicker by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = uiState.fechaSeleccionada,
                    onValueChange = { },
                    label = { Text("Fecha") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(Icons.Default.DateRange, "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (mostrarDatePicker) {
                    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
                    DatePickerDialog(
                        onDismissRequest = { mostrarDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        onFechaChanged(LocalDate.ofEpochDay(it / 86400000L))
                                    }
                                    mostrarDatePicker = false
                                }
                            ) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                
                // Slider para seleccionar horas (0-16)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Horas trabajadas: ${uiState.horasSeleccionadas}h",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = uiState.horasSeleccionadas.toFloat(),
                        onValueChange = { newValue -> 
                            // Redondear al entero más cercano y asegurar rango 0-16
                            val horas = newValue.roundToInt().coerceIn(0, 16)
                            onHorasChanged(horas)
                        },
                        valueRange = 0f..16f,
                        steps = 15, // 0-16: 17 valores, 16 steps
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0h", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("16h", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                OutlinedTextField(
                    value = uiState.observaciones,
                    onValueChange = onObservacionesChanged,
                    label = { Text("Observaciones (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !uiState.isLoading) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Icon(Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EditarEmpleadoDialog(
    uiState: DashboardUiState,
    onDismiss: () -> Unit,
    onLegajoChanged: (String) -> Unit,
    onNombreChanged: (String) -> Unit,
    onSectorChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDelete: () -> Unit,
    onEditarRegistroHoras: (com.registro.empleados.domain.model.RegistroAsistencia) -> Unit = {},
    onCerrarEdicionRegistroHoras: () -> Unit = {},
    onHorasEdicionChanged: (Int) -> Unit = {},
    onGuardarEdicionRegistroHoras: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Empleado") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(value = uiState.editarEmpleadoLegajo, onValueChange = onLegajoChanged, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = uiState.editarEmpleadoNombre, onValueChange = onNombreChanged, label = { Text("Nombre Completo") }, isError = uiState.editarEmpleadoNombre.isBlank(), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = uiState.editarEmpleadoSector, onValueChange = onSectorChanged, label = { Text("Sector") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text("Horas cargadas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                if (uiState.registrosParaEditar.isEmpty()) {
                    Text("No hay horas registradas en los últimos 6 meses", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    uiState.registrosParaEditar.forEach { registro ->
                        val fechaFormato = try {
                            val d = java.time.LocalDate.parse(registro.fecha)
                            d.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        } catch (_: Exception) { registro.fecha }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(fechaFormato, style = MaterialTheme.typography.bodyMedium)
                                Text("${registro.horasTrabajadas}h", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onEditarRegistroHoras(registro) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Edit, "Editar horas", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ELIMINAR EMPLEADO")
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = uiState.editarEmpleadoNombre.isNotBlank()) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
    if (uiState.registroEnEdicion != null) {
        AlertDialog(
            onDismissRequest = onCerrarEdicionRegistroHoras,
            title = { Text("Editar Horas") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Fecha: ${uiState.registroEnEdicion!!.fecha}", style = MaterialTheme.typography.bodyMedium)
                    Text("Horas: ${uiState.horasEdicion}h", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Slider(
                        value = uiState.horasEdicion.toFloat(),
                        onValueChange = { onHorasEdicionChanged(it.roundToInt().coerceIn(1, 16)) },
                        valueRange = 1f..16f,
                        steps = 14,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = onGuardarEdicionRegistroHoras) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = onCerrarEdicionRegistroHoras) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ConfirmarEliminarDialog(
    empleado: Empleado?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
        title = { Text("Confirmar Eliminación") },
        text = { Text("¿Seguro que deseas eliminar a ${empleado?.nombreCompleto}?\nEsta acción no se puede deshacer.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("SÍ, ELIMINAR") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}


@Composable
private fun ConfirmacionFlotante(
    mensajePrincipal: String,
    mensajeSecundario: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorFondo: Color,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        onDismiss()
    }
    
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp).fillMaxWidth(0.8f),
            colors = CardDefaults.cardColors(containerColor = colorFondo),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icono, null, tint = Color.White, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(mensajePrincipal, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(mensajeSecundario, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun EmpleadoCard(
    empleado: Empleado,
    tieneHorasCargadasHoy: Boolean,
    estaAusenteHoy: Boolean,
    isSelected: Boolean,
    onEditClick: () -> Unit,
    onRegistrarHorasClick: () -> Unit
) {
    val cardColors = remember(estaAusenteHoy, tieneHorasCargadasHoy) {
        when {
            estaAusenteHoy -> listOf(Color(0xFFFF5252), Color(0xFFD32F2F))
            tieneHorasCargadasHoy -> listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
            else -> listOf(Color(0xFF2A223C), Color(0xFF1E1E2E))
        }
    }
    val textColor = Color.White
    val clockIconColor = if (estaAusenteHoy || tieneHorasCargadasHoy) Color.White else Color(0xFF42EEDB)
    val editIconColor = if (estaAusenteHoy || tieneHorasCargadasHoy) Color.White else Color(0xFF9C27B0)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(colors = cardColors),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(empleado.nombreCompleto, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = textColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("DNI: ${empleado.legajo ?: "Sin datos"}", style = MaterialTheme.typography.bodyMedium, color = textColor.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sector: ${empleado.sector}", style = MaterialTheme.typography.bodyMedium, color = textColor.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Ingreso: ${empleado.fechaIngreso.format(dateFormatter)}", style = MaterialTheme.typography.bodyMedium, color = textColor.copy(alpha = 0.8f))
                    if (empleado.observacion != null && empleado.observacion.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Observación: ${empleado.observacion}", style = MaterialTheme.typography.bodyMedium, color = textColor.copy(alpha = 0.9f), fontWeight = FontWeight.Medium)
                    }
                    if (estaAusenteHoy) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("🚫 AUSENTE", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, "Seleccionado", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = onRegistrarHorasClick, enabled = !estaAusenteHoy) {
                        Icon(Icons.Default.Schedule, "Registrar horas", tint = clockIconColor)
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, "Editar empleado", tint = editIconColor)
                    }
                }
            }
        }
    }
}
