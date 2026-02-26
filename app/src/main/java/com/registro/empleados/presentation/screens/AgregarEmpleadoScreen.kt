@file:Suppress("DEPRECATION")

package com.registro.empleados.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import com.registro.empleados.presentation.viewmodel.EmpleadosViewModel
import kotlinx.coroutines.delay

/**
 * Pantalla para agregar un nuevo empleado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarEmpleadoScreen(
    windowSizeClass: WindowSizeClass,
    onNavigateBack: () -> Unit,
    viewModel: EmpleadosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Sistema de mensajes eliminado
    
    val padding = when (windowSizeClass.widthSizeClass) {
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact -> PaddingValues(16.dp)
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium -> PaddingValues(24.dp)
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> PaddingValues(32.dp)
        else -> PaddingValues(16.dp)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Agregar Empleado") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text = "Nuevo Empleado",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Campo DNI (obligatorio)
                OutlinedTextField(
                    value = uiState.legajo ?: "",
                    onValueChange = { viewModel.onLegajoChanged(it.ifBlank { null }) },
                    label = { Text("DNI *") },
                    placeholder = { Text("Ingrese el DNI") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    isError = (uiState.legajo.isNullOrBlank() && uiState.intentoGuardar) || uiState.error?.contains("legajo") == true,
                    supportingText = {
                        if (uiState.legajo.isNullOrBlank() && uiState.intentoGuardar) {
                            Text("El DNI es obligatorio", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Campo Nombre Completo
                OutlinedTextField(
                    value = uiState.nombreCompleto,
                    onValueChange = viewModel::onNombreCompletoChanged,
                    label = { Text("Nombre Completo *") },  // Asterisco para obligatorio
                    placeholder = { Text("Ej: JUAREZ JULIO") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.nombreCompleto.isBlank() && uiState.intentoGuardar,
                    supportingText = {
                        if (uiState.nombreCompleto.isBlank() && uiState.intentoGuardar) {
                            Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Campo Sector (estático)
                OutlinedTextField(
                    value = uiState.sector,
                    onValueChange = { }, // No se puede cambiar
                    label = { Text("Sector") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false, // Campo deshabilitado
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN GUARDAR
                Button(
                    onClick = { 
                        android.util.Log.d("AgregarEmpleado", "Click en Guardar")
                        viewModel.crearEmpleado() 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading && 
                             uiState.nombreCompleto.isNotBlank() &&
                             !uiState.legajo.isNullOrBlank()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Creando...")
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Empleado")
                    }
                }

                // Información adicional
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Los campos marcados con * son obligatorios",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "• El DNI es obligatorio",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "• La fecha de ingreso se establece automáticamente",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        
        // Sistema de mensajes eliminado
    }
}
