package com.registro.empleados.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase
import com.registro.empleados.domain.usecase.empleado.GetEmpleadoByLegajoUseCase
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase
import com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase
import com.registro.empleados.domain.usecase.ausencia.EmpleadoTieneAusenciaEnFechaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import android.util.Log
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EmpleadosViewModel @Inject constructor(
    private val getAllEmpleadosActivosUseCase: GetAllEmpleadosActivosUseCase,
    private val getEmpleadoByLegajoUseCase: GetEmpleadoByLegajoUseCase,
    private val insertEmpleadoUseCase: InsertEmpleadoUseCase,
    private val updateEmpleadoUseCase: UpdateEmpleadoUseCase,
    private val tieneHorasCargadasHoyUseCase: TieneHorasCargadasHoyUseCase,
    private val empleadoTieneAusenciaEnFechaUseCase: EmpleadoTieneAusenciaEnFechaUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmpleadosUiState())
    val uiState: StateFlow<EmpleadosUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val recargarTrigger = MutableSharedFlow<Unit>()
    
    private var isLoading = false
    private var yaInicializado = false

    init {
        if (!yaInicializado) {
            yaInicializado = true
            loadEmpleados()
        }
        observeSearchQuery()
        
        // OBSERVAR TRIGGER DE RECARGA
        viewModelScope.launch {
            recargarTrigger.collect {
                Log.d("EmpleadosVM", "🔄 Trigger de recarga detectado")
                loadEmpleados()
            }
        }
    }

    private fun loadEmpleados() {
        if (isLoading) return
        
        viewModelScope.launch {
            try {
                Log.d("EmpleadosVM", "═══ CARGANDO EMPLEADOS ═══")
                isLoading = true
                _uiState.update { it.copy(isLoading = true) }
                
                val sectorActual = appPreferences.getSectorSeleccionado()
                Log.d("EmpleadosVM", "Sector: $sectorActual")
                _uiState.update { it.copy(sector = sectorActual ?: "") }
                
                getAllEmpleadosActivosUseCase().collect { todosLosEmpleados ->
                    val empleadosDelSector = todosLosEmpleados.filter { empleado ->
                        empleado.sector.equals(sectorActual, ignoreCase = true)
                    }
                    
                    Log.d("EmpleadosVM", "Empleados del sector: ${empleadosDelSector.size}")
                    
                    val hoy = LocalDate.now()
                    
                    val empleadosConHorasHoy = mutableSetOf<String>()
                    for (empleado in empleadosDelSector) {
                        val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
                        if (tieneHorasCargadasHoyUseCase(legajoKey)) {
                            empleadosConHorasHoy.add(legajoKey)
                        }
                    }
                    
                    val empleadosAusentesHoy = mutableSetOf<String>()
                    for (empleado in empleadosDelSector) {
                        val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
                        
                        Log.d("EmpleadosVM", "🔍 Verificando ausencia para: ${empleado.nombreCompleto} ($legajoKey)")
                        
                        val tieneAusencia = empleadoTieneAusenciaEnFechaUseCase(legajoKey, hoy)
                        Log.d("EmpleadosVM", "🔍 Resultado del UseCase: $tieneAusencia")
                        
                        if (tieneAusencia) {
                            empleadosAusentesHoy.add(legajoKey)
                            Log.d("EmpleadosVM", "  ❌ AUSENTE HOY - AGREGADO AL SET")
                        } else {
                            Log.d("EmpleadosVM", "  ✅ Presente")
                        }
                    }
                    
                    Log.d("EmpleadosVM", "═══════════════════════")
                    Log.d("EmpleadosVM", "Total ausentes hoy: ${empleadosAusentesHoy.size}")
                    Log.d("EmpleadosVM", "Ausentes: $empleadosAusentesHoy")
                    Log.d("EmpleadosVM", "═══════════════════════")
                    
                    val empleadosOrdenados = empleadosDelSector.sortedBy { it.nombreCompleto }
                    
                    _uiState.update {
                        it.copy(
                            empleados = empleadosOrdenados,
                            empleadosConHorasHoy = empleadosConHorasHoy,
                            empleadosAusentesHoy = empleadosAusentesHoy,
                            isLoading = false
                        )
                    }
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar empleados: ${e.message}"
                    ) 
                }
            } finally {
                isLoading = false
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            combine(
                _uiState,
                _searchQuery
            ) { state, query ->
                val empleadosFiltrados = if (query.isBlank()) {
                    state.empleados
                } else {
                    state.empleados.filter { empleado ->
                        (empleado.legajo?.contains(query, ignoreCase = true) == true) ||
                        empleado.nombreCompleto.contains(query, ignoreCase = true)
                    }
                }
                state.copy(empleadosFiltrados = empleadosFiltrados)
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFormFieldChanged(
        legajo: String? = null,
        nombreCompleto: String? = null,
        sector: String? = null,
        fechaIngreso: LocalDate? = null
    ) {
        val currentForm = _uiState.value.formularioNuevoEmpleado
        _uiState.value = _uiState.value.copy(
            formularioNuevoEmpleado = currentForm.copy(
                legajo = legajo ?: currentForm.legajo,
                nombreCompleto = nombreCompleto ?: currentForm.nombreCompleto,
                sector = sector ?: currentForm.sector,
                fechaIngreso = fechaIngreso ?: currentForm.fechaIngreso
            ),
            error = null
        )
    }

    fun toggleFormularioNuevoEmpleado() {
        _uiState.value = _uiState.value.copy(
            mostrarFormularioNuevo = !_uiState.value.mostrarFormularioNuevo,
            error = null,
            mensaje = null
        )
    }

    fun guardarNuevoEmpleado() {
        val form = _uiState.value.formularioNuevoEmpleado
        
        if (!esFormularioValido(form)) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                insertEmpleadoUseCase(
                    legajo = form.legajo ?: "",
                    nombreCompleto = form.nombreCompleto,
                    sector = form.sector,
                    fechaIngreso = form.fechaIngreso
                )
                
                // LIMPIAR FORMULARIO COMPLETAMENTE
                _uiState.value = _uiState.value.copy(
                    mostrarFormularioNuevo = false,
                    formularioNuevoEmpleado = FormularioNuevoEmpleado(),
                    mensaje = "Empleado creado exitosamente",
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al crear empleado",
                    isLoading = false
                )
            }
        }
    }

    private fun esFormularioValido(form: FormularioNuevoEmpleado): Boolean {
        val errores = mutableListOf<String>()
        
        // El legajo ya no es obligatorio
        if (form.nombreCompleto.isBlank()) errores.add("El nombre completo es obligatorio")
        if (form.fechaIngreso.isAfter(LocalDate.now())) {
            errores.add("La fecha de ingreso no puede ser futura")
        }
        
        if (errores.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                error = errores.joinToString(", ")
            )
            return false
        }
        
        return true
    }

    fun onFechaIngresoChanged(fecha: LocalDate) {
        _uiState.value = _uiState.value.copy(
            formularioNuevoEmpleado = _uiState.value.formularioNuevoEmpleado.copy(
                fechaIngreso = fecha
            )
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            mensaje = null
        )
    }

    fun onLegajoChanged(legajo: String?) {
        _uiState.value = _uiState.value.copy(legajo = legajo?.trim()?.ifBlank { null })
    }

    fun onNombreCompletoChanged(nombreCompleto: String) {
        _uiState.value = _uiState.value.copy(nombreCompleto = nombreCompleto)
    }

    fun onSectorChanged(sector: String) {
        _uiState.value = _uiState.value.copy(sector = sector)
    }

    fun crearEmpleado() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true, 
                    error = null,
                    intentoGuardar = true
                )
                
                if (_uiState.value.legajo.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "El DNI es obligatorio")
                    return@launch
                }
                if (_uiState.value.nombreCompleto.isBlank()) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "El nombre completo es obligatorio")
                    return@launch
                }
                
                val legajoTrimmed = _uiState.value.legajo!!.trim().uppercase()
                val sectorActual = _uiState.value.sector.ifBlank { "Sin especificar" }
                
                val empleadoExistente = getEmpleadoByLegajoUseCase(legajoTrimmed)
                if (empleadoExistente != null) {
                    if (empleadoExistente.sector.equals(sectorActual, ignoreCase = true)) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "El empleado ya existe en su sector"
                        )
                        return@launch
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        empleadoExistenteParaTraspaso = empleadoExistente
                    )
                    return@launch
                }
                
                insertEmpleadoUseCase(
                    legajo = legajoTrimmed,
                    nombreCompleto = _uiState.value.nombreCompleto.trim(),
                    sector = sectorActual,
                    fechaIngreso = LocalDate.now()
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    empleadoCreadoExitosamente = true,
                    mensaje = "✅ Empleado creado exitosamente",
                    legajo = null,
                    nombreCompleto = "",
                    sector = "",
                    intentoGuardar = false,
                    formularioNuevoEmpleado = FormularioNuevoEmpleado()
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No se pudo crear el empleado: ${e.message}"
                )
            }
        }
    }

    fun confirmarTraspasoEmpleado() {
        val empleado = _uiState.value.empleadoExistenteParaTraspaso ?: return
        val sectorActual = _uiState.value.sector.ifBlank { "Sin especificar" }
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                updateEmpleadoUseCase(empleado.copy(sector = sectorActual))
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    empleadoExistenteParaTraspaso = null,
                    empleadoCreadoExitosamente = true,
                    mensaje = "✅ Empleado traspasado a $sectorActual",
                    legajo = null,
                    nombreCompleto = "",
                    sector = "",
                    intentoGuardar = false,
                    formularioNuevoEmpleado = FormularioNuevoEmpleado()
                )
                loadEmpleados()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    empleadoExistenteParaTraspaso = null,
                    error = "Error al traspasar: ${e.message}"
                )
            }
        }
    }

    fun cancelarTraspasoEmpleado() {
        _uiState.value = _uiState.value.copy(empleadoExistenteParaTraspaso = null)
    }

    fun resetEmpleadoCreado() {
        _uiState.value = _uiState.value.copy(empleadoCreadoExitosamente = null)
    }

    fun clearMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
    
    /**
     * Recarga los empleados y recalcula los colores.
     */
    fun recargarEmpleados() {
        viewModelScope.launch {
            loadEmpleados()
        }
    }
    
    // FUNCIÓN PÚBLICA PARA RECARGAR DESDE OTRAS PANTALLAS
    fun recargarDatos() {
        viewModelScope.launch {
            Log.d("EmpleadosVM", "🔄 Recarga manual solicitada")
            isLoading = false  // Forzar recarga
            recargarTrigger.emit(Unit)
        }
    }
    
    fun limpiarFormulario() {
        Log.d("EmpleadosVM", "🗑️ Limpiando formulario manualmente")
        _uiState.value = _uiState.value.copy(
            formularioNuevoEmpleado = FormularioNuevoEmpleado(),
            legajo = null,
            nombreCompleto = "",
            sector = ""
        )
    }
    
    // FUNCIÓN AGRESIVA PARA FORZAR RECARGA DE COLORES
    fun forzarRecargaColores() {
        viewModelScope.launch {
            Log.d("EmpleadosVM", "🔥 FORZANDO RECARGA DE COLORES")
            isLoading = false
            loadEmpleados()
        }
    }
}

data class EmpleadosUiState(
    val empleados: List<Empleado> = emptyList(),
    val empleadosFiltrados: List<Empleado> = emptyList(),
    val empleadosConHorasHoy: Set<String> = emptySet(),
    val empleadosAusentesHoy: Set<String> = emptySet(),
    val mostrarFormularioNuevo: Boolean = false,
    val formularioNuevoEmpleado: FormularioNuevoEmpleado = FormularioNuevoEmpleado(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val mensaje: String? = null,
    val empleadoCreadoExitosamente: Boolean? = null,
    val empleadoExistenteParaTraspaso: Empleado? = null,
    val legajo: String? = null,
    val nombreCompleto: String = "",
    val sector: String = "",
    val intentoGuardar: Boolean = false
)

data class FormularioNuevoEmpleado(
    val legajo: String? = null,
    val nombreCompleto: String = "",
    val sector: String = "",
    val fechaIngreso: LocalDate = LocalDate.now()
) {
    val estaCompleto: Boolean
        get() = nombreCompleto.isNotBlank() && sector.isNotBlank()
}