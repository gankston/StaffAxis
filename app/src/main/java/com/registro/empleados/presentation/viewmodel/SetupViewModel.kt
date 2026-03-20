package com.registro.empleados.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.data.device.DeviceIdentityManager
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.domain.model.Sector
import com.registro.empleados.domain.repository.SectorRepository
import com.registro.empleados.domain.usecase.sync.SyncEmpleadosFromApiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val sectorRepository: SectorRepository,
    private val deviceIdentityManager: DeviceIdentityManager,
    private val syncEmpleadosFromApiUseCase: SyncEmpleadosFromApiUseCase
) : ViewModel() {

    data class UiState(
        val selectedSector: Sector? = null,
        val isSyncingSectors: Boolean = false,
        val isSaving: Boolean = false,
        val error: String? = null,
        val configuracionGuardada: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val sectors: StateFlow<List<Sector>> =
        sectorRepository.observeSectors()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        syncSectors()
    }

    /** Al seleccionar un ítem del dropdown, guardar encargado y sector_id en preferencias. */
    fun onSectorSelected(sector: Sector) {
        _uiState.update { it.copy(selectedSector = sector, error = null) }
        val encargado = sector.encargado?.takeIf { it.isNotBlank() } ?: "Sin asignar"
        appPreferences.guardarConfiguracion(
            nombreEncargado = encargado,
            sector = sector.name,
            sectorId = sector.id
        )
    }

    fun syncSectors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncingSectors = true, error = null) }
            try {
                sectorRepository.fetchSectorsFromServer()
            } catch (e: Exception) {
                Log.e("SetupVM", "Error sync sectors", e)
                _uiState.update { it.copy(error = "Error de conexión: No se pudieron obtener los sectores") }
            } finally {
                _uiState.update { it.copy(isSyncingSectors = false) }
            }
        }
    }

    fun onContinuar() {
        viewModelScope.launch {
            val sector = uiState.value.selectedSector
            if (sector == null) {
                _uiState.update { it.copy(error = "Seleccione un encargado") }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val encargado = sector.encargado?.takeIf { it.isNotBlank() } ?: "Sin asignar"
                appPreferences.guardarConfiguracion(
                    nombreEncargado = encargado,
                    sector = sector.name,
                    sectorId = sector.id
                )

                deviceIdentityManager.registerWhenConfigSaved(encargado, sector.name)

                when (val result = syncEmpleadosFromApiUseCase()) {
                    is SyncEmpleadosFromApiUseCase.Result.Success -> {
                        _uiState.update { it.copy(configuracionGuardada = true) }
                    }
                    is SyncEmpleadosFromApiUseCase.Result.Error -> {
                        _uiState.update { it.copy(error = "No se pudieron cargar los empleados: ${result.message}") }
                    }
                }
            } catch (e: Exception) {
                Log.e("SetupVM", "Error guardando configuración", e)
                _uiState.update { it.copy(error = "Error al guardar: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}

