package com.registro.empleados.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.model.RegistroAsistencia
import com.registro.empleados.domain.usecase.empleado.BuscarEmpleadoSimpleUseCase
import com.registro.empleados.domain.usecase.sync.SyncEmpleadosFromApiUseCase
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase
import com.registro.empleados.domain.usecase.empleado.GetEmpleadoByLegajoUseCase
import com.registro.empleados.domain.usecase.empleado.InsertEmpleadoUseCase
import com.registro.empleados.domain.usecase.empleado.TieneHorasCargadasHoyUseCase
import com.registro.empleados.domain.usecase.LimpiarTodosLosRegistrosUseCase
import com.registro.empleados.domain.usecase.database.CorregirEmpleadosDBDirectoUseCase
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import com.registro.empleados.domain.repository.HorasEmpleadoMesRepository
import com.registro.empleados.domain.repository.EmpleadoRepository
import com.registro.empleados.data.local.dao.EmpleadoDao
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.data.remote.api.AusenciasApiService
import com.registro.empleados.data.device.DeviceIdentityManager
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
import android.content.Context
import android.widget.Toast
import com.registro.empleados.domain.exception.TransferConflictException

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val buscarEmpleadoSimpleUseCase: BuscarEmpleadoSimpleUseCase,
    private val getAllEmpleadosActivosUseCase: GetAllEmpleadosActivosUseCase,
    private val getEmpleadoByLegajoUseCase: GetEmpleadoByLegajoUseCase,
    private val insertEmpleadoUseCase: InsertEmpleadoUseCase,
    private val tieneHorasCargadasHoyUseCase: TieneHorasCargadasHoyUseCase,
    private val limpiarTodosLosRegistrosUseCase: LimpiarTodosLosRegistrosUseCase,
    private val corregirEmpleadosDBDirectoUseCase: CorregirEmpleadosDBDirectoUseCase,
    private val updateEmpleadoUseCase: com.registro.empleados.domain.usecase.empleado.UpdateEmpleadoUseCase,
    private val darDeBajaEmpleadoUseCase: com.registro.empleados.domain.usecase.empleado.DarDeBajaEmpleadoUseCase,
    private val updateEstadoEmpleadoUseCase: com.registro.empleados.domain.usecase.empleado.UpdateEstadoEmpleadoUseCase,
    private val registroAsistenciaRepository: RegistroAsistenciaRepository,
    private val horasEmpleadoMesRepository: HorasEmpleadoMesRepository,
    private val appPreferences: AppPreferences,
    private val syncEmpleadosFromApiUseCase: SyncEmpleadosFromApiUseCase,
    private val empleadoRepository: EmpleadoRepository,
    private val empleadoDao: EmpleadoDao,
    private val ausenciasApiService: AusenciasApiService,
    private val deviceIdentityManager: DeviceIdentityManager,
    @dagger.hilt.android.qualifiers.ApplicationContext private val appContext: android.content.Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    private var isLoading = false
    private var yaInicializado = false
    private var lastApiRefreshTime: Long = 0
    private val REFRESH_THRESHOLD_MS = 5 * 60 * 1000 // 5 minutos
    private val empleadosConHorasHoy = mutableSetOf<String>()
    private val empleadosAusentesHoyInternal = mutableSetOf<String>()
    
    init {
        if (!yaInicializado) {
            yaInicializado = true
            val sectorSeleccionado = appPreferences.getSectorSeleccionado() ?: "RUTA 5"
            _uiState.update { it.copy(nuevoEmpleadoSector = sectorSeleccionado) }
            
            // CORREGIR EMPLEADOS Y CARGAR DATOS
            viewModelScope.launch {
                try {
                    Log.d("DashboardVM", "🧹 Iniciando limpieza de duplicados...")
                    // Limpiar duplicados que pudieron crearse por syncs defectuosos
                    empleadoDao.borrarDuplicadosPorBackendId()
                    Log.d("DashboardVM", "✅ Limpieza de duplicados completada")
                } catch (e: Exception) {
                    Log.e("DashboardVM", "Error al limpiar duplicados", e)
                }
                
                corregirEmpleadosDBDirectoUseCase()
                cargarEmpleadosYInsertarDatosSiNecesario()
            }
        }
    }

    fun forzarRecargaCompleta() {
        val currentTime = System.currentTimeMillis()
        val shouldFetchApi = (currentTime - lastApiRefreshTime) > REFRESH_THRESHOLD_MS
        
        viewModelScope.launch {
            if (shouldFetchApi) {
                refreshData()
            } else {
                cargarEmpleadosYInsertarDatosSiNecesario()
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                Log.d("DashboardVM", "🔄 Refreshing data from API...")
                val sectorId = appPreferences.getSectorId()
                if (!sectorId.isNullOrBlank()) {
                    syncEmpleadosFromApiUseCase()
                    lastApiRefreshTime = System.currentTimeMillis()
                }
                cargarEmpleadosYInsertarDatosSiNecesario()
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error refreshing data", e)
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
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

                // --- CONSULTAR AUSENCIAS DE HOY DESDE EL API ---
                fetchAusenciasDeHoy()

                getAllEmpleadosActivosUseCase().collect { todosLosEmpleados ->
                    if (todosLosEmpleados.isEmpty()) {
                        cargarEmpleados()
                    } else {
                        val empleadosDelSector = todosLosEmpleados.filter { empleado ->
                            empleado.sector.equals(sectorActual, ignoreCase = true)
                        }
                        
                        // Calcular estados para colores - SIEMPRE recalcular
                        for (empleado in empleadosDelSector) {
                            val idReal = empleado.employeeIdBackend
                            if (idReal.isNullOrBlank()) continue
                            Log.d("DashboardVM", "🔍 Verificando empleado: ${empleado.nombreCompleto} ($idReal)")
                            if (tieneHorasCargadasHoyUseCase(idReal)) {
                                empleadosConHorasHoy.add(idReal)
                                Log.d("DashboardVM", "  ✅ Tiene horas hoy")
                            }
                            if (empleadosAusentesHoyInternal.contains(idReal)) {
                                Log.d("DashboardVM", "  🔴 Empleado AUSENTE hoy: ${empleado.nombreCompleto}")
                            }
                        }

                        val empleadosOrdenados = empleadosDelSector.sortedBy { it.nombreCompleto }
                        
                        _uiState.update {
                            it.copy(
                                empleados = empleadosOrdenados,
                                empleadosFiltrados = empleadosOrdenados,
                                empleadosConHorasHoy = empleadosConHorasHoy,
                                empleadosAusentesHoy = empleadosAusentesHoyInternal.toSet(),
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

    /**
     * Consulta el API de ausencias para obtener ausentes de HOY.
     * IMPORTANTE: primero asegura que el device_token exista en DataStore,
     * ya que el interceptor OkHttp lo lee de ahí para agregar el header X-Device-Token.
     * Si la API falla o no hay token, el set queda vacío (no bloquea la carga de empleados).
     */
    private suspend fun fetchAusenciasDeHoy() {
        try {
            val hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            Log.d("DashboardVM_AUSENCIAS", "╔═══ FETCH AUSENCIAS HOY ($hoy) ═══╗")

            // 1. Asegurar que el device_token esté disponible antes de hacer el request
            val tieneToken = deviceIdentityManager.ensureDeviceToken()
            Log.d("DashboardVM_AUSENCIAS", "  ensureDeviceToken() → tieneToken=$tieneToken")

            if (!tieneToken) {
                Log.w("DashboardVM_AUSENCIAS", "  ⚠️ No hay device_token disponible. No se puede consultar ausencias (se necesita X-Device-Token). Saltando.")
                Log.d("DashboardVM_AUSENCIAS", "╚═══════════════════════════════════════╝")
                return
            }

            // 2. Hacer el request con token disponible
            Log.d("DashboardVM_AUSENCIAS", "  📡 GET /api/absences?start_date=$hoy&end_date=$hoy")
            val response = ausenciasApiService.getAbsences(startDate = hoy, endDate = hoy)
            Log.d("DashboardVM_AUSENCIAS", "  HTTP ${response.code()}")

            if (response.isSuccessful) {
                val ausencias = response.body()?.absences ?: emptyList()
                Log.d("DashboardVM_AUSENCIAS", "  ✅ Ausencias recibidas: ${ausencias.size}")
                empleadosAusentesHoyInternal.clear()
                ausencias.forEach { ausencia ->
                    Log.d("DashboardVM_AUSENCIAS", "    🔴 employee_id=${ausencia.employeeId} (${ausencia.startDate} → ${ausencia.endDate})")
                    empleadosAusentesHoyInternal.add(ausencia.employeeId)
                }
                Log.d("DashboardVM_AUSENCIAS", "  Total ausentes hoy: ${empleadosAusentesHoyInternal.size} → $empleadosAusentesHoyInternal")
            } else {
                val errorBody = response.errorBody()?.string() ?: "sin cuerpo de error"
                Log.w("DashboardVM_AUSENCIAS", "  ⚠️ API respondió ${response.code()}: $errorBody")
                // No limpiamos el set por si ya había datos de una consulta previa exitosa
            }

            Log.d("DashboardVM_AUSENCIAS", "╚═══════════════════════════════════════╝")
        } catch (e: Exception) {
            Log.e("DashboardVM_AUSENCIAS", "❌ Error consultando ausencias (NO bloquea la carga): ${e.javaClass.simpleName} - ${e.message}", e)
            // Silencioso: no propagamos el error para no bloquear el dashboard
        }
    }


    /**
     * Sync limpio: limpia BD, descarga desde API (Turso) y recarga lista.
     * Si falla la API, deja la UI vacía.
     */
    fun forceSync() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        error = null,
                        empleados = emptyList(),
                        empleadosConHorasHoy = emptySet()
                    )
                }

                when (val result = syncEmpleadosFromApiUseCase()) {
                    is SyncEmpleadosFromApiUseCase.Result.Success -> {
                        cargarEmpleados()
                        _uiState.update { it.copy(isLoading = false) }
                    }
                    is SyncEmpleadosFromApiUseCase.Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Error de conexión: No se pudieron obtener los datos actualizados",
                                empleados = emptyList(),
                                empleadosConHorasHoy = emptySet()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error en forceSync", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error de conexión: No se pudieron obtener los datos actualizados",
                        empleados = emptyList(),
                        empleadosConHorasHoy = emptySet()
                    )
                }
            }
        }
    }

    private fun cargarEmpleados() {
        viewModelScope.launch {
            try {
                val sectorActual = appPreferences.getSectorSeleccionado() ?: _uiState.value.nuevoEmpleadoSector.ifBlank { "RUTA 5" }

                // --- CONSULTAR AUSENCIAS DE HOY DESDE EL API ---
                fetchAusenciasDeHoy()

                getAllEmpleadosActivosUseCase().collect { todosLosEmpleados ->
                    val empleadosDelSector = todosLosEmpleados.filter { empleado ->
                        empleado.sector.equals(sectorActual, ignoreCase = true)
                    }

                    // Calcular estados para colores - USAR LEGAJOKEY CONSISTENTE
                    for (empleado in empleadosDelSector) {
                        val idReal = empleado.employeeIdBackend
                        if (idReal.isNullOrBlank()) continue
                        Log.d("DashboardVM", "───────────────────────── Empleado: ${empleado.nombreCompleto} ($idReal)")
                        val tieneHoras = tieneHorasCargadasHoyUseCase(idReal)
                        if (tieneHoras) empleadosConHorasHoy.add(idReal)
                        if (empleadosAusentesHoyInternal.contains(idReal)) {
                            Log.d("DashboardVM", "  🔴 AUSENTE: ${empleado.nombreCompleto}")
                        }
                    }
                    
                    Log.d("DashboardVM", "════════════════════════════════")
                    Log.d("DashboardVM", "RESUMEN:")
                    Log.d("DashboardVM", "  Empleados con horas: ${empleadosConHorasHoy.size}")
                    Log.d("DashboardVM", "  Set completo horas: $empleadosConHorasHoy")
                    Log.d("DashboardVM", "  Empleados ausentes hoy: ${empleadosAusentesHoyInternal.size}")
                    Log.d("DashboardVM", "  Set ausentes: $empleadosAusentesHoyInternal")
                    Log.d("DashboardVM", "════════════════════════════════")

                    val empleadosOrdenados = empleadosDelSector.sortedBy { it.nombreCompleto }
                    
                    _uiState.update {
                        it.copy(
                            empleados = empleadosOrdenados,
                            empleadosFiltrados = empleadosOrdenados,
                            empleadosConHorasHoy = empleadosConHorasHoy,
                            empleadosAusentesHoy = empleadosAusentesHoyInternal.toSet(),
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
        // Permitir cualquier hora de 0 a 16
        if (horas in 0..16) {
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

    /** Mensaje para mostrar como Toast (evitar fallo silencioso). La UI lo consume y llama a clearToastMessageRegistroHoras(). */
    fun clearToastMessageRegistroHoras() {
        _uiState.update { it.copy(toastMessageRegistroHoras = null) }
    }

    fun guardarRegistro() {
        val empleado = _uiState.value.empleadoEncontrado
        if (empleado == null) {
            Log.e("DashboardVM", "Error: empleado no seleccionado al guardar (empleadoEncontrado es null)")
            _uiState.value = _uiState.value.copy(
                error = "Error: empleado no seleccionado.",
                toastMessageRegistroHoras = "Error: empleado no seleccionado."
            )
            return
        }
        val fechaStr = _uiState.value.fechaSeleccionada
        if (fechaStr.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Seleccione una fecha", toastMessageRegistroHoras = "Seleccione una fecha")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // id en la base de datos es TEXT (UUID del servidor). Usar siempre employeeIdBackend para API y Room.
                val employeeIdReal = empleado.employeeIdBackend
                if (employeeIdReal.isNullOrBlank()) {
                    Log.e("DashboardVM", "Error: ID de empleado no encontrado. empleado=${empleado.nombreCompleto} employeeIdBackend=${empleado.employeeIdBackend}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error: ID de empleado no encontrado. Sincronice la lista de empleados desde el menú.",
                        toastMessageRegistroHoras = "Error: ID de empleado no encontrado."
                    )
                    return@launch
                }

                val fecha = LocalDate.parse(fechaStr, DISPLAY_DATE_FORMATTER)

                // VERIFICAR SI YA TIENE HORAS PARA ESTA FECHA ESPECÍFICA
                val registrosExistentes = registroAsistenciaRepository.getRegistrosByLegajoAndFecha(employeeIdReal, fecha)
                if (registrosExistentes.isNotEmpty()) {
                    val mensaje = "Este empleado ya tiene ${registrosExistentes.first().horasTrabajadas}h cargadas para esta fecha. Puede haber sido cargado por otro capataz en otro sector. Use 'Editar horas' en la ficha del empleado para corregir."
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
                
                // employeeIdReal = ID del backend (Primary Key en API), usado en Room y Outbox
                val registro = RegistroAsistencia(
                    id = 0,
                    legajoEmpleado = employeeIdReal,
                    fecha = fecha.format(DATE_FORMATTER),
                    horasTrabajadas = horas,
                    observaciones = observaciones.ifBlank { null }
                )

                Log.d("DashboardVM", "INSERTANDO REGISTRO -> empleado: ${empleado.nombreCompleto}, employee_id: $employeeIdReal, fecha: ${registro.fecha}, horas: ${registro.horasTrabajadas}")
                registroAsistenciaRepository.insertRegistro(registro)
                Log.d("DashboardVM", "Registro insertado en Room y añadido al Outbox")
                
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
            observaciones = "",
            error = null
        )
    }

    fun abrirDialogoConfirmarCargaMasiva() {
        _uiState.update { it.copy(mostrarDialogoConfirmarCargaMasiva = true) }
    }

    fun cerrarDialogoConfirmarCargaMasiva() {
        _uiState.update { it.copy(mostrarDialogoConfirmarCargaMasiva = false) }
    }

    /**
     * Carga masiva: 8h a todos los empleados del sector que NO estén ausentes hoy.
     * Solo disponible cuando el sector tiene más de 150 empleados (campos grandes).
     */
    fun cargaMasivaCamposGrandes() {
        val empleados = _uiState.value.empleados
        if (empleados.size <= 150) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val hoy = LocalDate.now()
                val fechaStr = hoy.format(DATE_FORMATTER)
                var cargados = 0
                var omitidos = 0

                for (empleado in empleados) {
                    val employeeIdReal = empleado.employeeIdBackend
                    if (employeeIdReal.isNullOrBlank()) {
                        omitidos++
                        continue
                    }
                    val existentes = registroAsistenciaRepository.getRegistrosByLegajoAndFecha(employeeIdReal, hoy)
                    if (existentes.isNotEmpty()) {
                        omitidos++
                        continue
                    }
                    val registro = RegistroAsistencia(
                        id = 0,
                        legajoEmpleado = employeeIdReal,
                        fecha = fechaStr,
                        horasTrabajadas = 8,
                        observaciones = null
                    )
                    registroAsistenciaRepository.insertRegistro(registro)
                    cargados++
                }

                recalcularColoresEmpleados()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mostrarDialogoConfirmarCargaMasiva = false,
                        mostrarMensajeRegistroExitoso = true,
                        mensajeRegistroExitoso = "✅ Carga masiva: $cargados empleados con 8h. Omitidos: $omitidos (ya con horas)."
                    )
                }
                delay(3000)
                _uiState.update { it.copy(mostrarMensajeRegistroExitoso = false) }
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error carga masiva", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mostrarDialogoConfirmarCargaMasiva = false,
                        error = "Error en carga masiva: ${e.message}"
                    )
                }
            }
        }
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

                val legajoTrimmed = state.nuevoEmpleadoLegajo.trim().uppercase()
                val nombre = state.nuevoEmpleadoNombre.trim()
                val apellido = state.nuevoEmpleadoApellido.trim()
                val nombreCompleto = "$apellido $nombre"

                val empleadoExistente = getEmpleadoByLegajoUseCase(legajoTrimmed)
                if (empleadoExistente != null) {
                    if (empleadoExistente.sector.equals(sectorSeleccionado, ignoreCase = true)) {
                        _uiState.value = state.copy(error = "El empleado ya existe en su sector")
                        return@launch
                    }
                    _uiState.value = state.copy(empleadoExistenteParaTraspaso = empleadoExistente)
                    return@launch
                }

                val sectorId = appPreferences.getSectorId()
                if (sectorId.isNullOrBlank()) {
                    _uiState.value = state.copy(error = "Configure el sector antes de agregar empleados")
                    return@launch
                }

                val result = empleadoRepository.createEmployeeViaApi(
                    firstName = nombre,
                    lastName = apellido,
                    documentNumber = legajoTrimmed,
                    sectorId = sectorId,
                    sectorName = sectorSeleccionado
                )

                result.fold(
                    onSuccess = {
                        recalcularColoresEmpleados()
                        
                        _uiState.value = state.copy(
                            mostrarDialogoNuevoEmpleado = false,
                            mostrarMensajeEmpleadoCreado = true,
                            mensajeEmpleadoCreado = "✅ Empleado creado: $nombreCompleto (DNI: $legajoTrimmed)",
                            nuevoEmpleadoLegajo = "",
                            nuevoEmpleadoNombre = "",
                            nuevoEmpleadoApellido = "",
                            nuevoEmpleadoSector = sectorSeleccionado
                        )
                        
                        cargarEmpleados()
                    },
                    onFailure = { e ->
                        android.util.Log.e("DashboardViewModel", "Error al crear empleado en servidor: ${e.message}", e)
                        if (e is TransferConflictException) {
                            android.util.Log.w("DashboardViewModel", "CONFLICTO DETECTADO (409): Mostrando diálogo de traspaso")
                            Toast.makeText(appContext, "Empleado Duplicado Detectado", Toast.LENGTH_LONG).show()
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                empleadoExistenteParaTraspaso = e.existingEmployee,
                                error = null
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                error = "Error al crear en servidor: ${e.message}",
                                isLoading = false
                            )
                        }
                    }
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al crear empleado: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun confirmarTraspasoEmpleado() {
        val empleado = _uiState.value.empleadoExistenteParaTraspaso ?: return
        val sectorId = appPreferences.getSectorId() ?: ""
        val sectorSeleccionado = appPreferences.getSectorSeleccionado() ?: "RUTA 5"
        
        android.util.Log.i("DashboardViewModel", ">>> CONFIRMAR TRASPASO PULSADO para ${empleado.nombreCompleto} (DNI: ${empleado.dni ?: empleado.legajo}) <<<")
        Toast.makeText(appContext, "Iniciando traspaso en servidor...", Toast.LENGTH_SHORT).show()
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Traspaso REAL en el servidor
                val result = empleadoRepository.createEmployeeViaApi(
                    firstName = empleado.nombre,
                    lastName = empleado.apellido,
                    documentNumber = empleado.dni ?: empleado.legajo,
                    sectorId = sectorId,
                    sectorName = sectorSeleccionado,
                    forceTransfer = true
                )
                
                result.fold(
                    onSuccess = {
                        android.util.Log.i("DashboardViewModel", "TRASPASO API ÉXITO")
                        Toast.makeText(appContext, "¡Empleado traspasado correctamente!", Toast.LENGTH_LONG).show()
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            empleadoExistenteParaTraspaso = null,
                            mostrarDialogoNuevoEmpleado = false,
                            mostrarMensajeEmpleadoCreado = true,
                            mensajeEmpleadoCreado = "✅ Empleado traspasado a $sectorSeleccionado",
                            nuevoEmpleadoLegajo = "",
                            nuevoEmpleadoNombre = "",
                            nuevoEmpleadoApellido = "",
                            nuevoEmpleadoSector = sectorSeleccionado,
                            error = null
                        )
                        
                        viewModelScope.launch {
                            syncEmpleadosFromApiUseCase()
                            cargarEmpleados()
                            recalcularColoresEmpleados()
                        }
                    },
                    onFailure = { e ->
                        android.util.Log.e("DashboardViewModel", "FALLO EN TRASPASO API: ${e.message}", e)
                        Toast.makeText(appContext, "Error al traspasar: ${e.message}", Toast.LENGTH_LONG).show()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al traspasar: ${e.message}",
                            empleadoExistenteParaTraspaso = null
                        )
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("DashboardViewModel", "EXCEPCIÓN EN TRASPASO: ${e.message}", e)
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

    fun abrirDialogoEditarEmpleado(empleado: Empleado) {
        viewModelScope.launch {
            val idReal = empleado.employeeIdBackend ?: ""
            val hace6Meses = LocalDate.now().minusMonths(6)
            val hoy = LocalDate.now()
            val registros = registroAsistenciaRepository.getRegistrosByLegajoYRango(
                idReal,
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
                val idReal = empleado.employeeIdBackend ?: ""
                val hace6Meses = LocalDate.now().minusMonths(6)
                val hoy = LocalDate.now()
                val registros = registroAsistenciaRepository.getRegistrosByLegajoYRango(
                    idReal,
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
    
    
    /**
     * Recalcula los colores de las tarjetas de empleados.
     */
    private suspend fun recalcularColoresEmpleados() {
        val empleadosConHorasHoy = mutableSetOf<String>()
        
        for (empleado in _uiState.value.empleados) {
            val idReal = empleado.employeeIdBackend ?: continue
            if (tieneHorasCargadasHoyUseCase(idReal)) empleadosConHorasHoy.add(idReal)
        }

        // Volver a consultar ausencias para mantener el estado actualizado
        fetchAusenciasDeHoy()

        Log.d("DashboardVM", "♻️ recalcularColores - horas: ${empleadosConHorasHoy.size}, ausentes: ${empleadosAusentesHoyInternal.size}")

        _uiState.update {
            it.copy(
                empleadosConHorasHoy = empleadosConHorasHoy,
                empleadosAusentesHoy = empleadosAusentesHoyInternal.toSet()
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
    /** IDs de empleados (employeeIdBackend) que tienen ausencia registrada para HOY en el API de producción. */
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
    val empleadoExistenteParaTraspaso: Empleado? = null,
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
    val mostrarDialogoConfirmarCargaMasiva: Boolean = false,
    val toastMessageRegistroHoras: String? = null,
    val isRefreshing: Boolean = false,
) {
    val puedeRegistrarEntrada: Boolean = false
    val puedeRegistrarSalida: Boolean = false
}