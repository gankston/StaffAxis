package com.registro.empleados.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.model.RegistroAsistencia
import com.registro.empleados.domain.usecase.empleado.BuscarEmpleadoSimpleUseCase
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase
import com.registro.empleados.domain.usecase.ausencia.EmpleadoTieneAusenciaEnFechaUseCase
import com.registro.empleados.domain.usecase.LimpiarTodosLosRegistrosUseCase
import com.registro.empleados.domain.usecase.database.CorregirEmpleadosDBDirectoUseCase
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository
import com.registro.empleados.data.local.preferences.AppPreferences
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val buscarEmpleadoSimpleUseCase: BuscarEmpleadoSimpleUseCase,
    private val getAllEmpleadosActivosUseCase: GetAllEmpleadosActivosUseCase,
    private val insertEmpleadoUseCase: InsertEmpleadoUseCase,
    private val tieneHorasCargadasHoyUseCase: TieneHorasCargadasHoyUseCase,
    private val empleadoTieneAusenciaEnFechaUseCase: EmpleadoTieneAusenciaEnFechaUseCase,
    private val limpiarTodosLosRegistrosUseCase: LimpiarTodosLosRegistrosUseCase,
    private val corregirEmpleadosDBDirectoUseCase: CorregirEmpleadosDBDirectoUseCase,
    private val updateEmpleadoUseCase: com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase,
    private val darDeBajaEmpleadoUseCase: com.registro.empleados.domain.usecase.empleado.DarDeBajaEmpleadoUseCase,
    private val updateEstadoEmpleadoUseCase: com.registro.empleados.domain.usecase.empleado.UpdateEstadoEmpleadoUseCase,
    private val registroAsistenciaRepository: RegistroAsistenciaRepository,
    private val horasEmpleadoMesRepository: HorasEmpleadoMesRepository,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    private var isLoading = false
    private var yaInicializado = false
    
    init {
        if (!yaInicializado) {
            yaInicializado = true
            val sectorSeleccionado = appPreferences.getSectorSeleccionado() ?: "RUTA 5"
            _uiState.update { it.copy(nuevoEmpleadoSector = sectorSeleccionado) }
            
            // CORREGIR EMPLEADOS Y CARGAR DATOS
            viewModelScope.launch {
                corregirEmpleadosDBDirectoUseCase()
                cargarEmpleadosYInsertarDatosSiNecesario()
            }
        }
    }
    
    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private val DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        private const val MAX_OBSERVATIONS_LENGTH = 200
    }

    private fun cargarEmpleadosYInsertarDatosSiNecesario() {
        if (isLoading) return
        
        viewModelScope.launch {
            try {
                isLoading = true
                _uiState.update { it.copy(isLoading = true) }
                
                val sectorActual = appPreferences.getSectorSeleccionado() ?: _uiState.value.nuevoEmpleadoSector.ifBlank { "RUTA 5" }
                
                getAllEmpleadosActivosUseCase().collect { todosLosEmpleados ->
                    if (todosLosEmpleados.isEmpty()) {
                        cargarEmpleados()
                    } else {
                        val empleadosDelSector = todosLosEmpleados.filter { empleado ->
                            empleado.sector.equals(sectorActual, ignoreCase = true)
                        }
                        
                        // Calcular estados para colores - SIEMPRE recalcular
                        val hoy = LocalDate.now()
                        val empleadosConHorasHoy = mutableSetOf<String>()
                        val empleadosAusentesHoy = mutableSetOf<String>()
                        for (empleado in empleadosDelSector) {
                            val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
                            
                            Log.d("DashboardVM", "🔍 Verificando empleado: ${empleado.nombreCompleto} ($legajoKey)")
                            
                            if (tieneHorasCargadasHoyUseCase(legajoKey)) {
                                empleadosConHorasHoy.add(legajoKey)
                                Log.d("DashboardVM", "  ✅ Tiene horas hoy")
                            }
                            
                            if (empleadoTieneAusenciaEnFechaUseCase(legajoKey, hoy)) {
                                empleadosAusentesHoy.add(legajoKey)
                                Log.d("DashboardVM", "  ❌ AUSENTE HOY")
                            } else {
                                Log.d("DashboardVM", "  ✅ Presente")
                            }
                        }
                        
                        Log.d("DashboardVM", "═══════════════════════")
                        Log.d("DashboardVM", "Total ausentes hoy: ${empleadosAusentesHoy.size}")
                        Log.d("DashboardVM", "Ausentes: $empleadosAusentesHoy")
                        Log.d("DashboardVM", "═══════════════════════")

                        val empleadosOrdenados = empleadosDelSector.sortedBy { it.nombreCompleto }
                        
                        _uiState.update {
                            it.copy(
                                empleados = empleadosOrdenados,
                                empleadosFiltrados = empleadosOrdenados,
                                empleadosConHorasHoy = empleadosConHorasHoy,
                                empleadosAusentesHoy = empleadosAusentesHoy,
                                isLoading = false
                            )
                        }
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

    private fun cargarEmpleados() {
        viewModelScope.launch {
            try {
                val sectorActual = appPreferences.getSectorSeleccionado() ?: _uiState.value.nuevoEmpleadoSector.ifBlank { "RUTA 5" }
                
                getAllEmpleadosActivosUseCase().collect { todosLosEmpleados ->
                    val empleadosDelSector = todosLosEmpleados.filter { empleado ->
                        empleado.sector.equals(sectorActual, ignoreCase = true)
                    }

                    // Calcular estados para colores - USAR LEGAJOKEY CONSISTENTE
                    val hoy = LocalDate.now()
                    val empleadosConHorasHoy = mutableSetOf<String>()
                    val empleadosAusentesHoy = mutableSetOf<String>()
                    
                    Log.d("DashboardVM", "════════════════════════════════")
                    Log.d("DashboardVM", "CALCULANDO COLORES PARA ${empleadosDelSector.size} EMPLEADOS")
                    
                    for (empleado in empleadosDelSector) {
                        val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
                        
                        Log.d("DashboardVM", "─────────────────────────")
                        Log.d("DashboardVM", "Empleado: ${empleado.nombreCompleto}")
                        Log.d("DashboardVM", "  Legajo original: ${empleado.legajo}")
                        Log.d("DashboardVM", "  LegajoKey generado: $legajoKey")
                        
                        val tieneHoras = tieneHorasCargadasHoyUseCase(legajoKey)
                        Log.d("DashboardVM", "  Tiene horas hoy: $tieneHoras")
                        
                        if (tieneHoras) {
                            empleadosConHorasHoy.add(legajoKey)
                            Log.d("DashboardVM", "  ✅ AGREGADO AL SET DE HORAS")
                        }
                        
                        if (empleadoTieneAusenciaEnFechaUseCase(legajoKey, hoy)) {
                            empleadosAusentesHoy.add(legajoKey)
                            Log.d("DashboardVM", "  ❌ AGREGADO AL SET DE AUSENCIAS")
                        }
                    }
                    
                    Log.d("DashboardVM", "════════════════════════════════")
                    Log.d("DashboardVM", "RESUMEN:")
                    Log.d("DashboardVM", "  Empleados con horas: ${empleadosConHorasHoy.size}")
                    Log.d("DashboardVM", "  Set completo: $empleadosConHorasHoy")
                    Log.d("DashboardVM", "════════════════════════════════")

                    val empleadosOrdenados = empleadosDelSector.sortedBy { it.nombreCompleto }
                    
                    _uiState.update {
                        it.copy(
                            empleados = empleadosOrdenados,
                            empleadosFiltrados = empleadosOrdenados,
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
            }
        }
    }

    fun onBusquedaChanged(busqueda: String) {
        _uiState.value = _uiState.value.copy(busqueda = busqueda)
        
        val empleadosFiltrados = if (busqueda.isBlank()) {
            _uiState.value.empleados
        } else {
            _uiState.value.empleados.filter { empleado ->
                (empleado.legajo?.contains(busqueda, ignoreCase = true) == true) ||
                empleado.nombreCompleto.contains(busqueda, ignoreCase = true)
            }.sortedBy { it.nombreCompleto }
        }
        
        _uiState.value = _uiState.value.copy(empleadosFiltrados = empleadosFiltrados)
    }

    fun onLegajoChanged(legajo: String) {
        _uiState.value = _uiState.value.copy(legajo = legajo)
    }

    fun onNombreChanged(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre)
    }

    fun buscarEmpleado() {
        val currentState = _uiState.value
        val legajo = currentState.legajo.trim()
        val nombreCompleto = currentState.nombre.trim()
        
        if (legajo.isBlank() && nombreCompleto.isBlank()) {
            _uiState.value = currentState.copy(
                error = "Ingrese DNI o nombre para buscar"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Buscar por legajo O por nombre
                val empleado = buscarEmpleadoSimpleUseCase(legajo, nombreCompleto)
                
                if (empleado != null) {
                    _uiState.value = _uiState.value.copy(
                        empleadoEncontrado = empleado,
                        fechaSeleccionada = LocalDate.now().format(DISPLAY_DATE_FORMATTER),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Empleado no encontrado",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al buscar empleado: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun onFechaChanged(fecha: LocalDate) {
        _uiState.value = _uiState.value.copy(
            fechaSeleccionada = fecha.format(DISPLAY_DATE_FORMATTER)
        )
    }

    fun onHorasChanged(horas: Int) {
        // Permitir cualquier hora de 1 a 16
        if (horas in 1..16) {
            _uiState.value = _uiState.value.copy(horasSeleccionadas = horas)
        }
    }

    fun onObservacionesChanged(observaciones: String) {
        val observacionesLimitadas = if (observaciones.length <= MAX_OBSERVATIONS_LENGTH) {
            observaciones
        } else {
            observaciones.take(MAX_OBSERVATIONS_LENGTH)
        }
        _uiState.value = _uiState.value.copy(observaciones = observacionesLimitadas)
    }

    fun abrirDialogoRegistroHoras(empleado: Empleado) {
        _uiState.update {
            it.copy(
                empleadoEncontrado = empleado,
                fechaSeleccionada = LocalDate.now().format(DISPLAY_DATE_FORMATTER),
                horasSeleccionadas = 8,
                observaciones = "",
                mostrarDialogoRegistroHoras = true,
                error = null
            )
        }
    }

    fun guardarRegistro() {
        val empleado = _uiState.value.empleadoEncontrado ?: return
        val fechaStr = _uiState.value.fechaSeleccionada
        
        if (fechaStr.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Seleccione una fecha")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val fecha = LocalDate.parse(fechaStr, DISPLAY_DATE_FORMATTER)
                val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
                
                // VERIFICAR SI YA TIENE HORAS PARA ESTA FECHA ESPECÍFICA
                val registrosExistentes = registroAsistenciaRepository.getRegistrosByLegajoAndFecha(legajoKey, fecha)
                if (registrosExistentes.isNotEmpty()) {
                    val mensaje = "Ya tiene ${registrosExistentes.first().horasTrabajadas}h cargadas para ${fechaStr}. Edite o elimine el registro existente."
                    _uiState.value = _uiState.value.copy(
                        mostrarDialogoRegistroHoras = false,
                        mostrarMensajeRegistroDuplicado = true,
                        mensajeRegistroDuplicado = mensaje,
                        // Limpiar selección para que la tarjeta no quede marcada
                        empleadoEncontrado = null,
                        fechaSeleccionada = "",
                        horasSeleccionadas = 8,
                        observaciones = "",
                        isLoading = false
                    )

                    // Ocultar cartel luego de un breve tiempo
                    delay(2000)
                    _uiState.value = _uiState.value.copy(mostrarMensajeRegistroDuplicado = false)
                    return@launch
                }
                
                val horas = _uiState.value.horasSeleccionadas
                val observaciones = _uiState.value.observaciones
                
                val registro = RegistroAsistencia(
                    id = 0,
                    legajoEmpleado = legajoKey,
                    fecha = fecha.format(DATE_FORMATTER),
                    horasTrabajadas = horas,
                    observaciones = observaciones.ifBlank { null }
                )
                
                Log.d("DashboardVM", "INSERTANDO REGISTRO -> empleado: ${empleado.nombreCompleto}, key: ${legajoKey}, fecha: ${registro.fecha}, horas: ${registro.horasTrabajadas}, obs: ${registro.observaciones}")

                registroAsistenciaRepository.insertRegistro(registro)
                
                _uiState.value = _uiState.value.copy(
                    mostrarDialogoRegistroHoras = false,
                    mostrarMensajeRegistroExitoso = true,
                    mensajeRegistroExitoso = "✅ Registro guardado: ${empleado.nombreCompleto} - ${fechaStr} - ${horas}h",
                    isLoading = false,
                    legajo = "",
                    nombre = "",
                    empleadoEncontrado = null,
                    fechaSeleccionada = "",
                    horasSeleccionadas = 0,
                    observaciones = ""
                )
                
                // Recalcular colores después de registrar horas
                recalcularColoresEmpleados()
                
                delay(2000)
                _uiState.value = _uiState.value.copy(mostrarMensajeRegistroExitoso = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al guardar registro: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun cerrarDialogoRegistroHoras() {
        _uiState.value = _uiState.value.copy(
            mostrarDialogoRegistroHoras = false,
            empleadoEncontrado = null,
            fechaSeleccionada = "",
            horasSeleccionadas = 0,
            observaciones = ""
        )
    }

    fun mostrarDialogoNuevoEmpleado() {
        _uiState.value = _uiState.value.copy(mostrarDialogoNuevoEmpleado = true)
    }

    fun cerrarDialogoNuevoEmpleado() {
        val sectorSeleccionado = appPreferences.getSectorSeleccionado() ?: ""
        _uiState.value = _uiState.value.copy(
            mostrarDialogoNuevoEmpleado = false,
            nuevoEmpleadoLegajo = "",
            nuevoEmpleadoNombre = "",
            nuevoEmpleadoApellido = "",
            nuevoEmpleadoSector = sectorSeleccionado
        )
    }

    fun onNuevoEmpleadoLegajoChanged(legajo: String) {
        _uiState.value = _uiState.value.copy(nuevoEmpleadoLegajo = legajo)
    }

    fun onNuevoEmpleadoNombreChanged(nombre: String) {
        _uiState.value = _uiState.value.copy(nuevoEmpleadoNombre = nombre)
    }

    fun onNuevoEmpleadoApellidoChanged(apellido: String) {
        _uiState.value = _uiState.value.copy(nuevoEmpleadoApellido = apellido)
    }

    fun crearNuevoEmpleado() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val sectorSeleccionado = appPreferences.getSectorSeleccionado() ?: "RUTA 5"
                
                if (state.nuevoEmpleadoLegajo.isBlank()) {
                    _uiState.value = state.copy(error = "El DNI es obligatorio")
                    return@launch
                }
                if (state.nuevoEmpleadoNombre.isBlank() || state.nuevoEmpleadoApellido.isBlank()) {
                    _uiState.value = state.copy(error = "El nombre y apellido son obligatorios")
                    return@launch
                }

                val legajo = state.nuevoEmpleadoLegajo.trim()

                insertEmpleadoUseCase(
                    legajo = legajo,
                    nombreCompleto = "${state.nuevoEmpleadoApellido.trim()} ${state.nuevoEmpleadoNombre.trim()}",
                    sector = sectorSeleccionado,
                    fechaIngreso = LocalDate.now()
                )
                
                // Recalcular colores después de crear empleado
                recalcularColoresEmpleados()
                
                _uiState.value = state.copy(
                    mostrarDialogoNuevoEmpleado = false,
                    mostrarMensajeEmpleadoCreado = true,
                    mensajeEmpleadoCreado = "✅ Empleado creado: ${state.nuevoEmpleadoApellido.trim()} ${state.nuevoEmpleadoNombre.trim()} (DNI: $legajo)",
                    nuevoEmpleadoLegajo = "",
                    nuevoEmpleadoNombre = "",
                    nuevoEmpleadoApellido = "",
                    nuevoEmpleadoSector = sectorSeleccionado
                )
                
                cargarEmpleados()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al crear empleado: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun abrirDialogoEditarEmpleado(empleado: Empleado) {
        viewModelScope.launch {
            val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
            val hace6Meses = LocalDate.now().minusMonths(6)
            val hoy = LocalDate.now()
            val registros = registroAsistenciaRepository.getRegistrosByLegajoYRango(
                legajoKey,
                hace6Meses.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                hoy.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )
            _uiState.value = _uiState.value.copy(
                mostrarDialogoEditarEmpleado = true,
                empleadoParaEditar = empleado,
                editarEmpleadoLegajo = empleado.legajo ?: "",
                editarEmpleadoNombre = empleado.nombreCompleto,
                editarEmpleadoSector = empleado.sector,
                registrosParaEditar = registros
            )
        }
    }

    fun cerrarDialogoEditarEmpleado() {
        _uiState.value = _uiState.value.copy(
            mostrarDialogoEditarEmpleado = false,
            empleadoParaEditar = null,
            editarEmpleadoLegajo = "",
            editarEmpleadoNombre = "",
            editarEmpleadoSector = "",
            registrosParaEditar = emptyList(),
            registroEnEdicion = null
        )
    }

    fun abrirEdicionRegistroHoras(registro: RegistroAsistencia) {
        _uiState.value = _uiState.value.copy(
            registroEnEdicion = registro,
            horasEdicion = registro.horasTrabajadas
        )
    }

    fun cerrarEdicionRegistroHoras() {
        _uiState.value = _uiState.value.copy(registroEnEdicion = null)
    }

    fun onHorasEdicionChanged(horas: Int) {
        if (horas in 1..16) {
            _uiState.value = _uiState.value.copy(horasEdicion = horas)
        }
    }

    fun guardarEdicionRegistroHoras() {
        val registro = _uiState.value.registroEnEdicion ?: return
        val nuevasHoras = _uiState.value.horasEdicion
        viewModelScope.launch {
            try {
                registroAsistenciaRepository.updateRegistro(
                    registro.copy(horasTrabajadas = nuevasHoras)
                )
                val empleado = _uiState.value.empleadoParaEditar ?: return@launch
                val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
                val hace6Meses = LocalDate.now().minusMonths(6)
                val hoy = LocalDate.now()
                val registros = registroAsistenciaRepository.getRegistrosByLegajoYRango(
                    legajoKey,
                    hace6Meses.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    hoy.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                )
                _uiState.value = _uiState.value.copy(
                    registroEnEdicion = null,
                    registrosParaEditar = registros,
                    mostrarMensajeEmpleadoEditado = true,
                    mensajeEmpleadoEditado = "✅ Horas actualizadas: ${registro.fecha} - ${nuevasHoras}h"
                )
                recalcularColoresEmpleados()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al actualizar: ${e.message}")
            }
        }
    }

    fun onEditarEmpleadoLegajoChanged(legajo: String) {
        _uiState.value = _uiState.value.copy(editarEmpleadoLegajo = legajo)
    }

    fun onEditarEmpleadoNombreChanged(nombre: String) {
        _uiState.value = _uiState.value.copy(editarEmpleadoNombre = nombre)
    }

    fun onEditarEmpleadoSectorChanged(sector: String) {
        _uiState.value = _uiState.value.copy(editarEmpleadoSector = sector)
    }

    fun guardarEmpleadoEditado() {
        val empleadoActual = _uiState.value.empleadoParaEditar ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                Log.d("DashboardVM", "=== GUARDAR EMPLEADO EDITADO ===")
                Log.d("DashboardVM", "Empleado actual - ID: ${empleadoActual.id}, Legajo: ${empleadoActual.legajo}, Nombre: ${empleadoActual.nombreCompleto}")
                
                val nuevoLegajo = _uiState.value.editarEmpleadoLegajo.trim()
                val nuevoNombreCompleto = _uiState.value.editarEmpleadoNombre.trim()
                val nuevoSector = _uiState.value.editarEmpleadoSector.trim()
                
                Log.d("DashboardVM", "Nuevos valores - Legajo: '$nuevoLegajo', Nombre: '$nuevoNombreCompleto', Sector: '$nuevoSector'")
                
                if (nuevoNombreCompleto.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        error = "El nombre completo es obligatorio"
                    )
                    return@launch
                }
                
                val empleadoActualizado = empleadoActual.copy(
                    legajo = nuevoLegajo.ifBlank { null },
                    nombreCompleto = nuevoNombreCompleto,
                    sector = nuevoSector.ifBlank { empleadoActual.sector }
                )
                
                Log.d("DashboardVM", "Empleado actualizado - ID: ${empleadoActualizado.id}, Legajo: ${empleadoActualizado.legajo}, Nombre: ${empleadoActualizado.nombreCompleto}")
                
                updateEmpleadoUseCase(empleadoActualizado)
                
                Log.d("DashboardVM", "✅ UseCase ejecutado correctamente")
                
                _uiState.value = _uiState.value.copy(
                    mostrarDialogoEditarEmpleado = false,
                    empleadoParaEditar = null,
                    editarEmpleadoLegajo = "",
                    editarEmpleadoNombre = "",
                    editarEmpleadoSector = "",
                    mostrarMensajeEmpleadoEditado = true,
                    mensajeEmpleadoEditado = "✅ Empleado editado: ${nuevoNombreCompleto} (DNI: ${nuevoLegajo.ifBlank { "Sin DNI" }})",
                    isLoading = false
                )
                
                cargarEmpleados()
                
                delay(2000)
                _uiState.value = _uiState.value.copy(mostrarMensajeEmpleadoEditado = false)
                
            } catch (e: Exception) {
                Log.e("DashboardVM", "❌ Error al actualizar empleado", e)
                _uiState.value = _uiState.value.copy(
                    error = "Error al actualizar empleado: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun abrirDialogoConfirmarEliminar(empleado: Empleado) {
        _uiState.value = _uiState.value.copy(
            mostrarDialogoConfirmarEliminar = true,
            empleadoParaEliminar = empleado
        )
    }

    fun cerrarDialogoConfirmarEliminar() {
        _uiState.value = _uiState.value.copy(
            mostrarDialogoConfirmarEliminar = false,
            empleadoParaEliminar = null
        )
    }

    fun confirmarEliminarEmpleado() {
        val empleado = _uiState.value.empleadoParaEliminar ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Mostrar loading en hilo principal
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                }

                // Marcar como inactivo usando el ID (NO afecta registros de horas)
                if (empleado.legajo != null) {
                    darDeBajaEmpleadoUseCase(empleado.legajo)
                } else {
                    // Si no tiene legajo, actualizar estado por ID
                    updateEstadoEmpleadoUseCase(empleado.id, false)
                }

                // Actualizar UI en hilo principal - CERRAR DIALOGO INMEDIATAMENTE
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        mostrarDialogoConfirmarEliminar = false,  // CERRAR DIALOGO
                        // Cerrar también la hoja de edición si estaba abierta
                        mostrarDialogoEditarEmpleado = false,
                        empleadoParaEditar = null,
                        mostrarMensajeEliminacion = true,
                        empleadoParaEliminar = null,
                        isLoading = false
                    )

                    // Recargar empleados y colores
                    cargarEmpleados()
                }

                // Ocultar mensaje tras un breve tiempo
                kotlinx.coroutines.delay(2000)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(mostrarMensajeEliminacion = false)
                }
            } catch (e: Exception) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        error = "Error al eliminar empleado: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun cerrarMensajeEliminacion() {
        _uiState.value = _uiState.value.copy(mostrarMensajeEliminacion = false)
    }

    fun cerrarMensajeRegistroExitoso() {
        _uiState.value = _uiState.value.copy(mostrarMensajeRegistroExitoso = false)
    }

    fun cerrarMensajeEmpleadoCreado() {
        _uiState.value = _uiState.value.copy(mostrarMensajeEmpleadoCreado = false)
    }

    fun cerrarMensajeEmpleadoEditado() {
        _uiState.value = _uiState.value.copy(mostrarMensajeEmpleadoEditado = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMensaje() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }
    
    /**
     * Recalcula los colores de las tarjetas de empleados.
     */
    fun recargarColoresEmpleados() {
        viewModelScope.launch {
            recalcularColoresEmpleados()
        }
    }
    
    // FUNCIÓN PARA FORZAR RECARGA DESDE OTRAS PANTALLAS
    fun forzarRecargaCompleta() {
        viewModelScope.launch {
            Log.d("DashboardVM", "🔥 FORZANDO RECARGA COMPLETA")
            isLoading = false
            cargarEmpleados()
        }
    }
    
    /**
     * Recalcula los colores de las tarjetas de empleados.
     */
    private suspend fun recalcularColoresEmpleados() {
        val hoy = LocalDate.now()
        val empleadosConHorasHoy = mutableSetOf<String>()
        val empleadosAusentesHoy = mutableSetOf<String>()
        
        for (empleado in _uiState.value.empleados) {
            val legajoKey = empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
            
            if (tieneHorasCargadasHoyUseCase(legajoKey)) {
                empleadosConHorasHoy.add(legajoKey)
            }
            if (empleadoTieneAusenciaEnFechaUseCase(legajoKey, hoy)) {
                empleadosAusentesHoy.add(legajoKey)
            }
        }
        
        _uiState.update {
            it.copy(
                empleadosConHorasHoy = empleadosConHorasHoy,
                empleadosAusentesHoy = empleadosAusentesHoy
            )
        }
    }
}

data class DashboardUiState(
    val legajo: String = "",
    val nombre: String = "",
    val busqueda: String = "",
    val empleados: List<Empleado> = emptyList(),
    val empleadosFiltrados: List<Empleado> = emptyList(),
    val empleadosConHorasHoy: Set<String> = emptySet(),
    val empleadosAusentesHoy: Set<String> = emptySet(),
    val empleadoEncontrado: Empleado? = null,
    val registroHoy: RegistroAsistencia? = null,
    val fechaSeleccionada: String = "",
    val horasSeleccionadas: Int = 8,
    val observaciones: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null,
    val mostrarDialogoNuevoEmpleado: Boolean = false,
    val nuevoEmpleadoLegajo: String = "",
    val nuevoEmpleadoNombre: String = "",
    val nuevoEmpleadoApellido: String = "",
    val nuevoEmpleadoSector: String = "",
    val mostrarDialogoRegistroHoras: Boolean = false,
    val mostrarDialogoEditarEmpleado: Boolean = false,
    val empleadoParaEditar: Empleado? = null,
    val editarEmpleadoLegajo: String = "",
    val editarEmpleadoNombre: String = "",
    val editarEmpleadoSector: String = "",
    val registrosParaEditar: List<RegistroAsistencia> = emptyList(),
    val registroEnEdicion: RegistroAsistencia? = null,
    val horasEdicion: Int = 8,
    val mostrarDialogoConfirmarEliminar: Boolean = false,
    val empleadoParaEliminar: Empleado? = null,
    val mostrarMensajeEliminacion: Boolean = false,
    val mostrarMensajeRegistroExitoso: Boolean = false,
    val mensajeRegistroExitoso: String = "",
    val mostrarMensajeEmpleadoCreado: Boolean = false,
    val mensajeEmpleadoCreado: String = "",
    val mostrarMensajeEmpleadoEditado: Boolean = false,
    val mensajeEmpleadoEditado: String = "",
    // Cartel cuando se intenta cargar horas duplicadas en el mismo día
    val mostrarMensajeRegistroDuplicado: Boolean = false,
    val mensajeRegistroDuplicado: String = "",
) {
    val puedeRegistrarEntrada: Boolean = false
    val puedeRegistrarSalida: Boolean = false
}