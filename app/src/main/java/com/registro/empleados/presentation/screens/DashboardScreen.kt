package com.registro.empleados.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.registro.empleados.presentation.components.ResponsiveDashboard
import com.registro.empleados.presentation.viewmodel.DashboardViewModel
import kotlinx.coroutines.delay

/**
 * Pantalla principal del dashboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // RECARGAR cuando la pantalla se vuelve visible
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                android.util.Log.d("DashboardScreen", "🔄 Pantalla resumida - FORZANDO recarga de datos")
                viewModel.forzarRecargaCompleta()
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // Cartel de confirmación eliminado - ahora se usa el cartel flotante
    
        ResponsiveDashboard(
            windowSizeClass = windowSizeClass,
            uiState = uiState,
            onBusquedaChanged = viewModel::onBusquedaChanged,
            onAbrirDialogoRegistroHoras = viewModel::abrirDialogoRegistroHoras,
            onFechaChanged = viewModel::onFechaChanged,
            onHorasChanged = viewModel::onHorasChanged,
            onObservacionesChanged = viewModel::onObservacionesChanged,
            onGuardarRegistro = viewModel::guardarRegistro,
            onAgregarEmpleado = viewModel::mostrarDialogoNuevoEmpleado,
            onNuevoEmpleadoLegajoChanged = viewModel::onNuevoEmpleadoLegajoChanged,
            onNuevoEmpleadoNombreChanged = viewModel::onNuevoEmpleadoNombreChanged,
            onNuevoEmpleadoApellidoChanged = viewModel::onNuevoEmpleadoApellidoChanged,
            onCrearNuevoEmpleado = viewModel::crearNuevoEmpleado,
            onCerrarDialogoNuevoEmpleado = viewModel::cerrarDialogoNuevoEmpleado,
            onCerrarDialogoRegistroHoras = viewModel::cerrarDialogoRegistroHoras,
            onEditarEmpleado = viewModel::abrirDialogoEditarEmpleado,
            onEditarEmpleadoLegajoChanged = viewModel::onEditarEmpleadoLegajoChanged,
            onEditarEmpleadoNombreChanged = viewModel::onEditarEmpleadoNombreChanged,
            onEditarEmpleadoSectorChanged = viewModel::onEditarEmpleadoSectorChanged,
            onGuardarEmpleadoEditado = viewModel::guardarEmpleadoEditado,
            onCerrarDialogoEditarEmpleado = viewModel::cerrarDialogoEditarEmpleado,
            onAbrirDialogoConfirmarEliminar = viewModel::abrirDialogoConfirmarEliminar,
            onCerrarDialogoConfirmarEliminar = viewModel::cerrarDialogoConfirmarEliminar,
            onEliminarEmpleadoConfirmado = viewModel::confirmarEliminarEmpleado,
            onCerrarMensajeEliminacion = viewModel::cerrarMensajeEliminacion,
            onCerrarMensajeRegistroExitoso = viewModel::cerrarMensajeRegistroExitoso,
            onCerrarMensajeEmpleadoCreado = viewModel::cerrarMensajeEmpleadoCreado,
            onCerrarMensajeEmpleadoEditado = viewModel::cerrarMensajeEmpleadoEditado,
            modifier = Modifier.padding(16.dp)
        )
}