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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.registro.empleados.R
import com.registro.empleados.presentation.viewmodel.EmpleadosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadosScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: EmpleadosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.recargarEmpleados()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
    LaunchedEffect(Unit) {
        viewModel.limpiarFormulario()
    }

    val padding = when (windowSizeClass.widthSizeClass) {
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact -> PaddingValues(16.dp)
        else -> PaddingValues(24.dp)
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.recargarDatos() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_staffaxis),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                Column {
                    Text(text = "StaffAxis", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = "Carga de Horas", style = MaterialTheme.typography.titleMedium, color = Color(0xFFB0B0B0))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Buscar empleado", color = Color(0xFFB0B0B0)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF26C6DA)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF26C6DA),
                    focusedContainerColor = Color(0x33FFFFFF),
                    unfocusedContainerColor = Color(0x22FFFFFF),
                    focusedIndicatorColor = Color(0xFF26C6DA),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Empleados (${uiState.empleados.size})",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFB0B0B0),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            if (uiState.mostrarFormularioNuevo) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D243F))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Nuevo Empleado", color = Color.White, fontWeight = FontWeight.Bold)
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
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { viewModel.toggleFormularioNuevoEmpleado() }, modifier = Modifier.weight(1f)) {
                                Text("Cancelar")
                            }
                            Button(
                                onClick = { viewModel.guardarNuevoEmpleado() },
                                modifier = Modifier.weight(1f),
                                enabled = uiState.formularioNuevoEmpleado.estaCompleto && !uiState.isLoading
                            ) {
                                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                else Text("Guardar")
                            }
                        }
                    }
                }
            }
            
            LazyColumn(
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = uiState.empleadosFiltrados.ifEmpty { uiState.empleados }, key = { it.id }) { empleado ->
                    val idReal = empleado.employeeIdBackend
                    val tieneHoras = idReal?.let { uiState.empleadosConHorasHoy.contains(it) } == true
                    val esAusenteHoy = idReal?.let { uiState.empleadosAusentesHoy.contains(it) } == true
                    EmpleadoCard(
                        empleado = empleado,
                        tieneHorasCargadasHoy = tieneHoras,
                        esAusenteHoy = esAusenteHoy,
                        onInfoClick = { /* Ver info del empleado */ },
                        onEditClick = { viewModel.abrirDialogoEditar(empleado) }
                    )
                }
            }
        }
        
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            FloatingActionButton(
                onClick = { viewModel.toggleFormularioNuevoEmpleado() },
                containerColor = Color.Transparent,
                modifier = Modifier.padding(32.dp).size(64.dp).background(
                    brush = Brush.radialGradient(colors = listOf(Color(0xFFE91E63), Color(0xFF9C27B0))),
                    shape = CircleShape
                )
            ) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) }
        }
    }

    if (uiState.empleadoExistenteParaTraspaso != null) {
        val empleado = uiState.empleadoExistenteParaTraspaso!!
        AlertDialog(
            onDismissRequest = { viewModel.cancelarTraspasoEmpleado() },
            title = { Text("Empleado ya registrado", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("El empleado ${empleado.nombreCompleto} (DNI: ${empleado.dni ?: "N/A"}) ya está en el sector ${empleado.sector.uppercase()}.", color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("¿Desea traspasarlo a ${uiState.sector.uppercase()}?", color = Color.White)
                    Text("Aviso: Se moverá a su sector actual.", color = Color(0xFFFF4081), style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.guardarNuevoEmpleado(forceTransfer = true) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))) { Text("Sí, traspasar") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelarTraspasoEmpleado() }) { Text("Cancelar", color = Color.White) }
            },
            containerColor = Color(0xFF1E1E2E),
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (uiState.empleadoCreadoExitosamente == true) {
        AlertDialog(
            onDismissRequest = { viewModel.resetEmpleadoCreado() },
            title = { Text("¡Éxito!", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(uiState.mensaje ?: "Operación completada.", color = Color.White) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetEmpleadoCreado() }) { Text("ACEPTAR", color = Color(0xFF26C6DA)) }
            },
            containerColor = Color(0xFF1E1E2E),
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (uiState.mostrarDialogoEditar && uiState.empleadoAEditar != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogoEditar() },
            title = { Text("Editar Empleado", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.editarNombreCompleto,
                        onValueChange = { viewModel.onEditarNombreChanged(it) },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = uiState.editarLegajo ?: "",
                        onValueChange = { viewModel.onEditarLegajoChanged(it) },
                        label = { Text("DNI") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.guardarEmpleadoEditado() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26C6DA)),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    else Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cerrarDialogoEditar() }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E1E2E),
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (uiState.mostrarMensajeEditado) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarMensajeEditado() },
            title = { Text("✅ Actualizado", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(uiState.mensajeEditado, color = Color.White) },
            confirmButton = {
                TextButton(onClick = { viewModel.cerrarMensajeEditado() }) { Text("ACEPTAR", color = Color(0xFF26C6DA)) }
            },
            containerColor = Color(0xFF1E1E2E),
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
fun EmpleadoCard(
    empleado: com.registro.empleados.domain.model.Empleado,
    tieneHorasCargadasHoy: Boolean,
    esAusenteHoy: Boolean,
    onInfoClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val cardColor = when {
        esAusenteHoy -> listOf(Color(0xFFFF5252), Color(0xFFD32F2F))
        tieneHorasCargadasHoy -> listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
        else -> listOf(Color(0xFF2A223C), Color(0xFF1E1E2E))
    }
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(cardColor), RoundedCornerShape(20.dp)).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = empleado.nombreCompleto.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = "DNI: ${empleado.dni ?: "---"}", color = Color.White.copy(0.8f), style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onInfoClick) { Icon(Icons.Default.Schedule, null, tint = Color.White) }
                IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, null, tint = Color.White) }
            }
        }
    }
}