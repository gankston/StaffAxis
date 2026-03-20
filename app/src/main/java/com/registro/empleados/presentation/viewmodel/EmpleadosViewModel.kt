package com.registro.empleados.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.repository.EmpleadoRepository
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase
import com.registro.empleados.domain.usecase.empleado.GetEmpleadoByLegajoUseCase
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase
import com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByFechaUseCase
import com.registro.empleados.domain.usecase.sync.SyncEmpleadosFromApiUseCase
import com.registro.empleados.domain.exception.TransferConflictException
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import android.util.Log
import android.widget.Toast
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
    private val getAusenciasByFechaUseCase: GetAusenciasByFechaUseCase,
    private val appPreferences: AppPreferences,
    private val empleadoRepository: EmpleadoRepository,
    private val syncEmpleadosFromApiUseCase: SyncEmpleadosFromApiUseCase,
    @ApplicationContext private val appContext: Context
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
                        val idReal = empleado.employeeIdBackend ?: continue
                        if (tieneHorasCargadasHoyUseCase(idReal)) empleadosConHorasHoy.add(idReal)
                    }

                    val ausentesHoy = try {
                        getAusenciasByFechaUseCase(hoy)
                            .asSequence()
                            .map { it.legajoEmpleado }
                            .filter { it.isNotBlank() }
                            .toSet()
                    } catch (e: Exception) {
                        Log.w("EmpleadosVM", "No se pudo obtener ausencias de hoy: ${e.message}")
                        emptySet()
                    }
                    
                    val empleadosOrdenados = empleadosDelSector.sortedBy { it.nombreCompleto }
                    
                    _uiState.update {
                        it.copy(
                            empleados = empleadosOrdenados,
                            empleadosConHorasHoy = empleadosConHorasHoy,
                            empleadosAusentesHoy = ausentesHoy,
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

    fun guardarNuevoEmpleado(forceTransfer: Boolean = false) {
        android.util.Log.i("EmpleadosViewModel", ">>> BOTÓN GUARDAR PULSADO (forceTransfer=$forceTransfer) <<<")
        // Toast para debug (se quitará después)
        Toast.makeText(appContext, "Guardando empleado...", Toast.LENGTH_SHORT).show()
        val form = _uiState.value.formularioNuevoEmpleado
        
        if (!forceTransfer && !esFormularioValido(form)) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val sectorId = appPreferences.getSectorId() ?: ""
                val sectorName = appPreferences.getSectorSeleccionado() ?: ""
                
                if (sectorId.isBlank()) {
                    _uiState.update { it.copy(isLoading = false, error = "Configure el sector antes de continuar") }
                    return@launch
                }

                // Usamos la API para crear/traspasar
                val result = empleadoRepository.createEmployeeViaApi(
                    firstName = form.nombreCompleto.split(" ").lastOrNull() ?: form.nombreCompleto,
                    lastName = form.nombreCompleto.split(" ").firstOrNull() ?: "",
                    documentNumber = form.legajo,
                    sectorId = sectorId,
                    sectorName = sectorName,
                    forceTransfer = forceTransfer
                )

                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(
                            mostrarFormularioNuevo = false,
                            formularioNuevoEmpleado = FormularioNuevoEmpleado(),
                            mensaje = if (forceTransfer) "Empleado traspasado correctamente" else "Empleado agregado correctamente",
                            empleadoCreadoExitosamente = true,
                            empleadoExistenteParaTraspaso = null,
                            isLoading = false
                        )}
                        viewModelScope.launch {
                            syncEmpleadosFromApiUseCase()
                            loadEmpleados()
                        }
                    },
                    onFailure = { e ->
                        android.util.Log.e("EmpleadosViewModel", "Error al guardar empleado: ${e.message}", e)
                        if (e is TransferConflictException) {
                            android.util.Log.w("EmpleadosViewModel", "CONFLICTO DETECTADO: Mostrando pop-up")
                            Toast.makeText(appContext, "Empleado Duplicado Detectado", Toast.LENGTH_LONG).show()
                            _uiState.update { it.copy(
                                isLoading = false,
                                empleadoExistenteParaTraspaso = e.existingEmployee,
                                error = null
                            )}
                        } else {
                            android.util.Log.e("EmpleadosViewModel", "Otro error: ${e.message}")
                            Toast.makeText(appContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            _uiState.update { it.copy(
                                isLoading = false,
                                error = e.message ?: "Error al procesar empleado"
                            )}
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "Error inesperado",
                    isLoading = false
                )}
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

    fun onNombreChanged(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre.trim())
    }

    fun onApellidoChanged(apellido: String) {
        _uiState.value = _uiState.value.copy(apellido = apellido.trim())
    }

    fun onNombreCompletoChanged(nombreCompleto: String) {
        _uiState.value = _uiState.value.copy(nombreCompleto = nombreCompleto)
    }

    fun onSectorChanged(sector: String) {
        _uiState.value = _uiState.value.copy(sector = sector)
    }

    fun testRedCrearEmpleado() {
        Log.e("GASTON_DEBUG", "TEST RED: iniciado")
        Toast.makeText(appContext, "TEST RED: iniciando POST...", Toast.LENGTH_LONG).show()
        viewModelScope.launch {
            try {
                val sectorId = appPreferences.getSectorId()
                if (sectorId.isNullOrBlank()) {
                    Toast.makeText(appContext, "TEST RED: falta sector_id", Toast.LENGTH_LONG).show()
                    return@launch
                }
                val sectorName = appPreferences.getSectorSeleccionado()
                val result = empleadoRepository.createEmployeeViaApi(
                    firstName = "Pepe",
                    lastName = "Test",
                    documentNumber = "000",
                    sectorId = sectorId,
                    sectorName = sectorName
                )
                result.fold(
                    onSuccess = {
                        android.util.Log.i("EmpleadosViewModel", "TEST RED ÉXITO")
                        Toast.makeText(appContext, "TEST RED OK: creado", Toast.LENGTH_LONG).show()
                        loadEmpleados()
                    },
                    onFailure = { e ->
                        android.util.Log.e("EmpleadosViewModel", "TEST RED FALLO: ${e.message}", e)
                        if (e is TransferConflictException) {
                            android.util.Log.w("EmpleadosViewModel", "CONFLICTO DETECTADO en TEST RED")
                            Toast.makeText(appContext, "Test de red: ¡Empleado Duplicado!", Toast.LENGTH_LONG).show()
                            _uiState.update { it.copy(
                                isLoading = false,
                                empleadoExistenteParaTraspaso = e.existingEmployee,
                                error = null
                            )}
                        } else {
                            Toast.makeText(appContext, "TEST RED FAIL: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(appContext, "TEST RED EXC: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun crearEmpleado() {
        val nombre = _uiState.value.nombre
        Log.e("DEBUG_GASTON", "Botón presionado")
        Toast.makeText(appContext, "Intentando enviar a: $nombre", Toast.LENGTH_SHORT).show()
        println("!!! GASTON_DEBUG: entrar crearEmpleado() nombre=${_uiState.value.nombre} apellido=${_uiState.value.apellido} dni=${_uiState.value.legajo}")

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    intentoGuardar = true
                )

                val sectorId = appPreferences.getSectorId()
                if (sectorId.isNullOrBlank()) {
                    Toast.makeText(appContext, "No enviado: Configure el sector antes de agregar empleados", Toast.LENGTH_LONG).show()
                    println("!!! GASTON_DEBUG: abortar: sectorId vacío")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Configure el sector antes de agregar empleados"
                    )
                    return@launch
                }
                val sectorName = appPreferences.getSectorSeleccionado()

                if (_uiState.value.legajo.isNullOrBlank()) {
                    Toast.makeText(appContext, "No enviado: El DNI es obligatorio", Toast.LENGTH_LONG).show()
                    println("!!! GASTON_DEBUG: abortar: DNI vacío")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "El DNI es obligatorio")
                    return@launch
                }
                if (_uiState.value.nombre.isBlank()) {
                    Toast.makeText(appContext, "No enviado: El nombre es obligatorio", Toast.LENGTH_LONG).show()
                    println("!!! GASTON_DEBUG: abortar: nombre vacío")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "El nombre es obligatorio")
                    return@launch
                }
                if (_uiState.value.apellido.isBlank()) {
                    Toast.makeText(appContext, "No enviado: El apellido es obligatorio", Toast.LENGTH_LONG).show()
                    println("!!! GASTON_DEBUG: abortar: apellido vacío")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "El apellido es obligatorio")
                    return@launch
                }

                val legajoTrimmed = _uiState.value.legajo!!.trim()
                android.util.Log.d("EmpleadosVM", "DNI capturado del formulario para POST: \"$legajoTrimmed\"")
                println("!!! GASTON_DEBUG: payload listo -> first_name=${_uiState.value.nombre.trim()} last_name=${_uiState.value.apellido.trim()} dni=$legajoTrimmed sector_id=$sectorId")
                val sectorActual = sectorName.ifBlank { "Sin especificar" }

                val empleadoExistente = getEmpleadoByLegajoUseCase(legajoTrimmed.uppercase())
                if (empleadoExistente != null) {
                    if (empleadoExistente.sector.equals(sectorActual, ignoreCase = true)) {
                        Toast.makeText(appContext, "No enviado: El empleado ya existe en su sector", Toast.LENGTH_LONG).show()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "El empleado ya existe en su sector"
                        )
                        return@launch
                    }
                    Toast.makeText(appContext, "No enviado: Empleado existe en otro sector (traspaso)", Toast.LENGTH_LONG).show()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        empleadoExistenteParaTraspaso = empleadoExistente
                    )
                    return@launch
                }

                val result = empleadoRepository.createEmployeeViaApi(
                    firstName = _uiState.value.nombre.trim(),
                    lastName = _uiState.value.apellido.trim(),
                    documentNumber = legajoTrimmed,
                    sectorId = sectorId,
                    sectorName = sectorName
                )

                result.fold(
                    onSuccess = {
                        println("!!! GASTON_DEBUG: API OK (createEmployeeViaApi) -> éxito")
                        _uiState.update { it.copy(
                            isLoading = false,
                            empleadoCreadoExitosamente = true,
                            mensaje = "Empleado agregado correctamente",
                            legajo = null,
                            nombre = "",
                            apellido = "",
                            nombreCompleto = "",
                            intentoGuardar = false,
                            formularioNuevoEmpleado = FormularioNuevoEmpleado(),
                            error = null
                        )}
                        viewModelScope.launch {
                            syncEmpleadosFromApiUseCase()
                            loadEmpleados()
                        }
                    },
                    onFailure = { e ->
                        android.util.Log.e("EmpleadosViewModel", "Error en crearEmpleado: ${e.message}", e)
                        if (e is TransferConflictException) {
                            android.util.Log.w("EmpleadosViewModel", "CONFLICTO DETECTADO (409): Mostrando pop-up")
                            Toast.makeText(appContext, "Empleado Duplicado Detectado", Toast.LENGTH_LONG).show()
                            _uiState.update { it.copy(
                                isLoading = false,
                                empleadoExistenteParaTraspaso = e.existingEmployee,
                                error = null
                            )}
                        } else {
                            val errorMsg = e.message ?: "Error al crear empleado"
                            android.util.Log.e("EmpleadosViewModel", "Otro error (API): $errorMsg")
                            Toast.makeText(appContext, "Error (API): $errorMsg", Toast.LENGTH_LONG).show()
                            _uiState.update { it.copy(isLoading = false, error = errorMsg) }
                        }
                    }
                )
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Error al crear empleado"
                Toast.makeText(appContext, "No enviado (excepción): $errorMsg", Toast.LENGTH_LONG).show()
                println("!!! GASTON_DEBUG: Error en ViewModel (excepción): ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMsg
                )
            }
        }
    }

    fun confirmarTraspasoEmpleado() {
        val empleado = _uiState.value.empleadoExistenteParaTraspaso ?: return
        val sectorId = appPreferences.getSectorId()
        val sectorName = appPreferences.getSectorSeleccionado()
        if (sectorId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                empleadoExistenteParaTraspaso = null,
                error = "Configure el sector"
            )
            return
        }
        viewModelScope.launch {
            try {
                android.util.Log.i("EmpleadosViewModel", ">>> CONFIRMAR TRASPASO PULSADO para ${empleado.nombreCompleto} (DNI: ${empleado.dni ?: empleado.legajo}) <<<")
                Toast.makeText(appContext, "Iniciando traspaso...", Toast.LENGTH_SHORT).show()
                _uiState.value = _uiState.value.copy(isLoading = true)
                val result = empleadoRepository.createEmployeeViaApi(
                    firstName = empleado.nombre,
                    lastName = empleado.apellido,
                    documentNumber = empleado.dni ?: empleado.legajo,
                    sectorId = sectorId,
                    sectorName = sectorName,
                    forceTransfer = true // AQUÍ FORZAMOS EL TRASPASO YA CONFIRMADO
                )
                result.fold(
                    onSuccess = {
                        android.util.Log.i("EmpleadosViewModel", "TRASPASO ÉXITO")
                        Toast.makeText(appContext, "¡Empleado traspasado correctamente!", Toast.LENGTH_LONG).show()
                        _uiState.update { it.copy(
                            isLoading = false,
                            empleadoExistenteParaTraspaso = null,
                            empleadoCreadoExitosamente = true,
                            mensaje = "Empleado traspasado a $sectorName correctamente",
                            legajo = null,
                            nombre = "",
                            apellido = "",
                            nombreCompleto = "",
                            intentoGuardar = false,
                            formularioNuevoEmpleado = FormularioNuevoEmpleado(),
                            error = null
                        )}
                        viewModelScope.launch {
                            syncEmpleadosFromApiUseCase()
                            loadEmpleados()
                        }
                    },
                    onFailure = { e ->
                        android.util.Log.e("EmpleadosViewModel", "FALLO EN TRASPASO: ${e.message}", e)
                        Toast.makeText(appContext, "Fallo en traspaso: ${e.message}", Toast.LENGTH_LONG).show()
                        _uiState.update { it.copy(
                            isLoading = false,
                            empleadoExistenteParaTraspaso = null,
                            error = "Error al confirmar traspaso: ${e.message}"
                        )}
                    }
                )
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
            nombre = "",
            apellido = "",
            nombreCompleto = "",
            sector = ""
        )
    }

    // --- EDICIÓN DE EMPLEADO ---

    fun abrirDialogoEditar(empleado: Empleado) {
        _uiState.update {
            it.copy(
                mostrarDialogoEditar = true,
                empleadoAEditar = empleado,
                editarNombreCompleto = empleado.nombreCompleto,
                editarLegajo = empleado.legajo
            )
        }
    }

    fun cerrarDialogoEditar() {
        _uiState.update {
            it.copy(
                mostrarDialogoEditar = false,
                empleadoAEditar = null,
                editarNombreCompleto = "",
                editarLegajo = null
            )
        }
    }

    fun onEditarNombreChanged(nuevoNombre: String) {
        _uiState.update { it.copy(editarNombreCompleto = nuevoNombre) }
    }

    fun onEditarLegajoChanged(nuevoLegajo: String) {
        _uiState.update { it.copy(editarLegajo = nuevoLegajo.ifBlank { null }) }
    }

    fun guardarEmpleadoEditado() {
        val empleadoActual = _uiState.value.empleadoAEditar ?: return
        val nuevoNombre = _uiState.value.editarNombreCompleto.trim()
        val nuevoLegajo = _uiState.value.editarLegajo?.trim()

        if (nuevoNombre.isBlank()) {
            _uiState.update { it.copy(error = "El nombre es obligatorio") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val empleadoActualizado = empleadoActual.copy(
                    nombreCompleto = nuevoNombre,
                    legajo = nuevoLegajo
                )

                updateEmpleadoUseCase(empleadoActualizado)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mostrarDialogoEditar = false,
                        empleadoAEditar = null,
                        mensajeEditado = "✅ Empleado actualizado: $nuevoNombre",
                        mostrarMensajeEditado = true
                    )
                }
                
                // Recargar lista
                loadEmpleados()
                
                kotlinx.coroutines.delay(2000)
                _uiState.update { it.copy(mostrarMensajeEditado = false) }

            } catch (e: Exception) {
                Log.e("EmpleadosVM", "Error al actualizar empleado", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al actualizar: ${e.message}"
                    )
                }
            }
        }
    }

    fun cerrarMensajeEditado() {
        _uiState.update { it.copy(mostrarMensajeEditado = false) }
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
    val nombre: String = "",
    val apellido: String = "",
    val nombreCompleto: String = "",
    val sector: String = "",
    val intentoGuardar: Boolean = false,
    
    // Edición de empleado
    val mostrarDialogoEditar: Boolean = false,
    val empleadoAEditar: Empleado? = null,
    val editarNombreCompleto: String = "",
    val editarLegajo: String? = null,
    val mostrarMensajeEditado: Boolean = false,
    val mensajeEditado: String = ""
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