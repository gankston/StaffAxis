package com.registro.empleados.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.data.device.DeviceIdentityManager
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.domain.model.EncargadoSector
import com.registro.empleados.domain.usecase.sync.SyncEmpleadosFromApiUseCase
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
    private val syncEmpleadosFromApiUseCase: SyncEmpleadosFromApiUseCase,
    private val deviceIdentityManager: DeviceIdentityManager
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
                
                // Registrar dispositivo en backend (POST /auth/device/register) y guardar token + sector_id
                deviceIdentityManager.registerWhenConfigSaved(encargado.nombreEncargado, encargado.sector)
                
                Log.d("BienvenidaVM", "Configuración guardada exitosamente")
                
                // Llamar a API real: GET /api/employees?sector_id={id} y poblar lista desde Turso
                when (val result = syncEmpleadosFromApiUseCase()) {
                    is SyncEmpleadosFromApiUseCase.Result.Success -> {
                        Log.d("BienvenidaVM", "Sync empleados desde API OK")
                        _uiState.update {
                            it.copy(isLoading = false, configuracionGuardada = true, error = null)
                        }
                    }
                    is SyncEmpleadosFromApiUseCase.Result.Error -> {
                        Log.e("BienvenidaVM", "Sync empleados falló: ${result.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                configuracionGuardada = false,
                                error = "No se pudieron cargar los empleados: ${result.message}"
                            )
                        }
                    }
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
