package com.registro.empleados.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.domain.usecase.empleado.CargarEmpleadosPorSectorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BienvenidaViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val cargarEmpleadosPorSectorUseCase: CargarEmpleadosPorSectorUseCase
) : ViewModel() {

    data class BienvenidaUiState(
        val nombreEncargado: String = "",
        val sectorSeleccionado: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val configuracionGuardada: Boolean = false
    )
    
    private val _uiState = MutableStateFlow(BienvenidaUiState())
    val uiState: StateFlow<BienvenidaUiState> = _uiState.asStateFlow()
    
    fun onNombreChanged(nombre: String) {
        val nombreCapitalizado = nombre.split(" ")
            .joinToString(" ") { palabra ->
                palabra.lowercase().replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                }
            }
        _uiState.update { it.copy(nombreEncargado = nombreCapitalizado, error = null) }
    }
    
    fun onSectorChanged(sector: String) {
        _uiState.update { it.copy(sectorSeleccionado = sector, error = null) }
    }
    
    fun guardarConfiguracion() {
        viewModelScope.launch {
            val nombreEncargado = _uiState.value.nombreEncargado.trim()
            val sector = _uiState.value.sectorSeleccionado
            
            // Validaciones
            if (nombreEncargado.isBlank()) {
                _uiState.update { it.copy(error = "Por favor ingrese su nombre") }
                return@launch
            }
            
            if (sector.isBlank()) {
                _uiState.update { it.copy(error = "Por favor seleccione un sector") }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                Log.d("BienvenidaVM", "Guardando configuración: $nombreEncargado - $sector")
                
                // Guardar en SharedPreferences
                appPreferences.guardarConfiguracion(nombreEncargado, sector)
                
                Log.d("BienvenidaVM", "Configuración guardada exitosamente")
                
                // Cargar empleados por sector después de guardar configuración
                Log.d("BienvenidaVM", "Cargando empleados por sector...")
                val resultadoCarga = cargarEmpleadosPorSectorUseCase()
                Log.d("BienvenidaVM", "Resultado carga empleados: $resultadoCarga")
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        configuracionGuardada = true
                    )
                }
            } catch (e: Exception) {
                Log.e("BienvenidaVM", "Error al guardar configuración", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al guardar: ${e.message}"
                    )
                }
            }
        }
    }
}
