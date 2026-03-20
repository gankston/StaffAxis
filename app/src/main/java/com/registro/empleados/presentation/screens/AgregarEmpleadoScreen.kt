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
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var validationDialogMessage by remember { mutableStateOf<String?>(null) }
    var autoTestFired by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Long
            )
        }
    }

    // BYPASS / TEST DE VIDA: dispara un POST hardcodeado al abrir la pantalla (una sola vez)
    LaunchedEffect(Unit) {
        if (!autoTestFired) {
            autoTestFired = true
            println("!!! DEBUG_GASTON: BYPASS AUTO POST al abrir pantalla (testRedCrearEmpleado) !!!")
            Toast.makeText(context, "Auto-Test de red: enviando POST...", Toast.LENGTH_LONG).show()
            viewModel.testRedCrearEmpleado()
        }
    }

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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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

                // Nombre
                OutlinedTextField(
                    value = uiState.nombre,
                    onValueChange = viewModel::onNombreChanged,
                    label = { Text("Nombre *") },
                    placeholder = { Text("Ej: Julio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.nombre.isBlank() && uiState.intentoGuardar,
                    supportingText = {
                        if (uiState.nombre.isBlank() && uiState.intentoGuardar) {
                            Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Apellido
                OutlinedTextField(
                    value = uiState.apellido,
                    onValueChange = viewModel::onApellidoChanged,
                    label = { Text("Apellido *") },
                    placeholder = { Text("Ej: Juárez") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.apellido.isBlank() && uiState.intentoGuardar,
                    supportingText = {
                        if (uiState.apellido.isBlank() && uiState.intentoGuardar) {
                            Text("El apellido es obligatorio", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // DNI / Documento
                OutlinedTextField(
                    value = uiState.legajo ?: "",
                    onValueChange = { viewModel.onLegajoChanged(it.ifBlank { null }) },
                    label = { Text("DNI / Documento *") },
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

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN GUARDAR
                Button(
                    onClick = {
                        println("!!! GASTON_DEBUG: BOTON PRESIONADO !!!")
                        Log.d("GASTON_DEBUG", "Clic en Guardar detectado")
                        Log.e("GASTON_DEBUG", "¡BOTON PRESIONADO!")
                        Toast.makeText(context, "Enviando...", Toast.LENGTH_LONG).show()
                        println("!!! GASTON_DEBUG: Datos -> Nombre: ${uiState.nombre}, Apellido: ${uiState.apellido}, DNI: ${uiState.legajo}")
                        if (uiState.nombre.isBlank()) {
                            Log.e("GASTON_DEBUG", "Fallo validación: nombre vacío")
                            Toast.makeText(context, "Falta completar el nombre", Toast.LENGTH_LONG).show()
                            validationDialogMessage = "Falta completar el nombre"
                            println("!!! GASTON_DEBUG: Fallo validación: nombre vacío")
                            return@Button
                        }
                        if (uiState.apellido.isBlank()) {
                            Log.e("GASTON_DEBUG", "Fallo validación: apellido vacío")
                            Toast.makeText(context, "Falta completar el apellido", Toast.LENGTH_LONG).show()
                            validationDialogMessage = "Falta completar el apellido"
                            println("!!! GASTON_DEBUG: Fallo validación: apellido vacío")
                            return@Button
                        }
                        if (uiState.legajo.isNullOrBlank()) {
                            Log.e("GASTON_DEBUG", "Fallo validación: DNI vacío")
                            Toast.makeText(context, "Falta completar el DNI", Toast.LENGTH_LONG).show()
                            validationDialogMessage = "Falta completar el DNI"
                            println("!!! GASTON_DEBUG: Fallo validación: DNI vacío")
                            return@Button
                        }
                        viewModel.crearEmpleado()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
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

                if (!uiState.error.isNullOrBlank()) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                TextButton(
                    onClick = {
                        Log.d("GASTON_DEBUG", "Test de Red presionado")
                        Toast.makeText(context, "Test de red: enviando POST hardcodeado...", Toast.LENGTH_LONG).show()
                        viewModel.testRedCrearEmpleado()
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Test de Red (temporal)")
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
                            text = "• El empleado se asigna al sector en el que trabaja el encargado",
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
        
        if (uiState.empleadoExistenteParaTraspaso != null) {
            AlertDialog(
                onDismissRequest = { viewModel.cancelarTraspasoEmpleado() },
                title = { Text("Empleado ya registrado") },
                text = {
                    Column {
                        Text("El empleado ${uiState.empleadoExistenteParaTraspaso!!.nombreCompleto} (DNI: ${uiState.empleadoExistenteParaTraspaso!!.legajo}) ya está dado de alta en el sector ${uiState.empleadoExistenteParaTraspaso!!.sector}.")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("¿Desea agregarlo a su sector (${uiState.sector})?", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Aviso: No se eliminará del otro sector, se traspasará a su sector.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.confirmarTraspasoEmpleado() }) { Text("Sí, traspasar") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.cancelarTraspasoEmpleado() }) { Text("Cancelar") }
                }
            )
        }

        if (validationDialogMessage != null) {
            AlertDialog(
                onDismissRequest = { validationDialogMessage = null },
                title = { Text("Faltan datos") },
                text = { Text(validationDialogMessage ?: "") },
                confirmButton = {
                    Button(onClick = { validationDialogMessage = null }) { Text("OK") }
                }
            )
        }
    }
}
