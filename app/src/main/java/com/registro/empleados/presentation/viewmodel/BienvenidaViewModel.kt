package com.registro.empleados.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.domain.model.EncargadoSector
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
        val mostrarSelectorEncargado: Boolean = false,
        val encargadoSeleccionado: EncargadoSector? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val configuracionGuardada: Boolean = false
    )
    
    private val _uiState = MutableStateFlow(BienvenidaUiState())
    val uiState: StateFlow<BienvenidaUiState> = _uiState.asStateFlow()
    
    fun onContinuarClicked() {
        _uiState.update { it.copy(mostrarSelectorEncargado = true, error = null) }
    }
    
    fun onEncargadoSelected(encargado: EncargadoSector) {
        _uiState.update { it.copy(encargadoSeleccionado = encargado, error = null) }
        guardarConfiguracion(encargado)
    }
    
    private fun guardarConfiguracion(encargado: EncargadoSector) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                Log.d("BienvenidaVM", "Guardando configuración: ${encargado.nombreEncargado} - ${encargado.sector}")
                
                appPreferences.guardarConfiguracion(encargado.nombreEncargado, encargado.sector)
                
                Log.d("BienvenidaVM", "Configuración guardada exitosamente")
                
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
