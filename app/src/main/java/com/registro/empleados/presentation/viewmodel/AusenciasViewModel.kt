package com.registro.empleados.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.data.remote.api.AusenciasApiService
import com.registro.empleados.data.remote.dto.CreateAbsenceRequestDto
import com.registro.empleados.data.device.DeviceIdentityManager
import com.registro.empleados.domain.model.Ausencia
import com.registro.empleados.domain.model.DiaLaboral
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.model.PeriodoLaboral
import com.registro.empleados.domain.usecase.calendario.GetDiasLaboralesUseCase
import com.registro.empleados.domain.usecase.calendario.GetPeriodoActualUseCase
import com.registro.empleados.domain.usecase.calendario.GenerarDiasLaboralesBasicosUseCase
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByRangoUseCase
import com.registro.empleados.domain.usecase.ausencia.GetAusenciasByFechaUseCase
import com.registro.empleados.domain.usecase.ausencia.CrearAusenciaUseCase
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AusenciasViewModel @Inject constructor(
    private val getDiasLaboralesUseCase: GetDiasLaboralesUseCase,
    private val getPeriodoActualUseCase: GetPeriodoActualUseCase,
    private val generarDiasLaboralesBasicosUseCase: GenerarDiasLaboralesBasicosUseCase,
    private val getAusenciasByRangoUseCase: GetAusenciasByRangoUseCase,
    private val getAusenciasByFechaUseCase: GetAusenciasByFechaUseCase,
    private val crearAusenciaUseCase: CrearAusenciaUseCase,
    private val actualizarAusenciaUseCase: com.registro.empleados.domain.usecase.ausencia.ActualizarAusenciaUseCase,
    private val eliminarAusenciaUseCase: com.registro.empleados.domain.usecase.ausencia.EliminarAusenciaUseCase,
    private val getAllEmpleadosActivosUseCase: GetAllEmpleadosActivosUseCase,
    private val appPreferences: AppPreferences,
    private val ausenciasApiService: AusenciasApiService,
    private val deviceIdentityManager: DeviceIdentityManager
) : ViewModel() {

    data class AusenciasUiState(
        val diasLaborales: List<DiaLaboral> = emptyList(),
        val periodoActual: PeriodoLaboral = PeriodoLaboral(
            fechaInicio = LocalDate.now(),
            fechaFin = LocalDate.now()
        ),
        val ausencias: List<Ausencia> = emptyList(),
        val fechasConAusencias: Set<LocalDate> = emptySet(),
        val fechaSeleccionada: LocalDate? = null,
        val ausentesEnFechaSeleccionada: List<Ausencia> = emptyList(),
        val mostrarDialogoAgregar: Boolean = false,
        val mostrarDialogoEditar: Boolean = false,
        val mostrarDetallesFecha: Boolean = false,
        val ausenciaParaEditar: Ausencia? = null,
        val ausenciaParaEliminar: Ausencia? = null,
        val mostrarConfirmacionEliminar: Boolean = false,
        val empleadosDelSector: List<Empleado> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val mensaje: String? = null,
        val ausenciaCreada: Boolean = false  // Flag para notificar que se creó una ausencia
    )
    
    private val _uiState = MutableStateFlow(AusenciasUiState())
    val uiState: StateFlow<AusenciasUiState> = _uiState.asStateFlow()
    
    // Variables para controlar el estado de carga
    private var isLoading = false
    private var hasLoadedInitialData = false
    private var yaInicializado = false  // AGREGAR ESTE FLAG
    
    init {
        Log.d("AusenciasVM", "INIT - AusenciasViewModel inicializado")
        // Solo cargar una vez al inicializar
        if (!yaInicializado) {
            Log.d("AusenciasVM", "INIT - Primera vez, cargando datos...")
            yaInicializado = true
            cargarCalendario()
        } else {
            Log.d("AusenciasVM", "INIT - Ya inicializado, saltando carga")
        }
    }
    
    fun cargarCalendario() {
        // Permitir recarga para cambio de mes
        if (isLoading && hasLoadedInitialData) {
            Log.d("AusenciasVM", "Ya hay una carga en progreso, ignorando...")
            return
        }
        
        viewModelScope.launch {
            try {
                isLoading = true
                Log.d("AusenciasVM", "═══ CARGANDO CALENDARIO Y AUSENCIAS ═══")
                
                _uiState.update { it.copy(isLoading = true) }
                
                // Usar el período seleccionado en UI si existe; de lo contrario obtener el actual
                val periodo = _uiState.value.periodoActual.let { seleccionado ->
                    if (seleccionado.fechaInicio <= seleccionado.fechaFin) seleccionado else try {
                        getPeriodoActualUseCase()
                    } catch (e: Exception) {
                        PeriodoLaboral(
                            fechaInicio = LocalDate.now().withDayOfMonth(26).minusMonths(if (LocalDate.now().dayOfMonth < 26) 1 else 0),
                            fechaFin = LocalDate.now().withDayOfMonth(25).plusMonths(if (LocalDate.now().dayOfMonth >= 26) 1 else 0)
                        )
                    }
                }
                
                // Obtener días laborales solo del mes actual
                var dias = try {
                    // Usar solo el mes actual para evitar duplicación
                    val primerDiaDelMes = periodo.fechaInicio.withDayOfMonth(1)
                    val ultimoDiaDelMes = primerDiaDelMes.withDayOfMonth(primerDiaDelMes.lengthOfMonth())
                    getDiasLaboralesUseCase(primerDiaDelMes, ultimoDiaDelMes)
                } catch (e: Exception) {
                    emptyList()
                }
                
                // Si no hay días laborales, generarlos
                if (dias.isEmpty()) {
                    Log.d("AusenciasVM", "No hay días laborales, generando...")
                    try {
                        // Generar días para cada mes del período
                        var fechaActual = periodo.fechaInicio
                        while (!fechaActual.isAfter(periodo.fechaFin)) {
                            generarDiasLaboralesBasicosUseCase(fechaActual.year, fechaActual.monthValue)
                            fechaActual = fechaActual.plusMonths(1)
                        }
                        
                        // Intentar obtener los días nuevamente
                        dias = getDiasLaboralesUseCase(periodo.fechaInicio, periodo.fechaFin)
                        Log.d("AusenciasVM", "Días laborales generados: ${dias.size}")
                    } catch (e: Exception) {
                        Log.e("AusenciasVM", "Error generando días laborales", e)
                    }
                }
                
                // Obtener TODAS las ausencias del período
                val ausencias = getAusenciasByRangoUseCase(periodo.fechaInicio, periodo.fechaFin)
                
                // Crear set de fechas con ausencias (para mostrar puntos rojos)
                val fechasConAusencias = mutableSetOf<LocalDate>()
                ausencias.forEach { ausencia ->
                    fechasConAusencias.addAll(ausencia.getFechasAfectadas())
                }
                
                // Cargar empleados del sector
                val sectorActual = appPreferences.getSectorSeleccionado()
                Log.d("AusenciasVM", "Sector actual: $sectorActual")
                
                val todosLosEmpleados = getAllEmpleadosActivosUseCase().first()
                Log.d("AusenciasVM", "Total empleados en BD: ${todosLosEmpleados.size}")
                
                val empleadosDelSector = todosLosEmpleados.filter { 
                    it.sector.equals(sectorActual, ignoreCase = true) 
                }
                
                Log.d("AusenciasVM", "Ausencias en período: ${ausencias.size}")
                Log.d("AusenciasVM", "Fechas con ausentes: ${fechasConAusencias.size}")
                Log.d("AusenciasVM", "Empleados del sector '$sectorActual': ${empleadosDelSector.size}")
                
                // Log de algunos empleados para debug
                empleadosDelSector.take(3).forEach { empleado ->
                    Log.d("AusenciasVM", "Empleado: ${empleado.nombreCompleto} - Sector: ${empleado.sector}")
                }
                
                _uiState.update {
                    it.copy(
                        diasLaborales = dias,
                        periodoActual = periodo,
                        ausencias = ausencias,
                        fechasConAusencias = fechasConAusencias,
                        empleadosDelSector = empleadosDelSector,
                        isLoading = false
                    )
                }
                
                hasLoadedInitialData = true
                Log.d("AusenciasVM", "✅ Calendario cargado exitosamente")
                
            } catch (e: Exception) {
                Log.e("AusenciasVM", "Error cargando calendario", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            } finally {
                isLoading = false
            }
        }
    }
    
    fun onFechaClick(fecha: LocalDate) {
        viewModelScope.launch {
            try {
                Log.d("AusenciasVM", "Click en fecha: $fecha")
                
                // Obtener ausentes de esa fecha
                val ausentesEnFecha = getAusenciasByFechaUseCase(fecha)
                
                _uiState.update {
                    it.copy(
                        fechaSeleccionada = fecha,
                        ausentesEnFechaSeleccionada = ausentesEnFecha,
                        mostrarDetallesFecha = ausentesEnFecha.isNotEmpty()
                    )
                }
                
            } catch (e: Exception) {
                Log.e("AusenciasVM", "Error obteniendo ausentes", e)
            }
        }
    }
    
    fun mostrarDialogoAgregarAusencia() {
        _uiState.update { it.copy(mostrarDialogoAgregar = true) }
    }
    
    fun cerrarDialogoAgregarAusencia() {
        _uiState.update { it.copy(mostrarDialogoAgregar = false) }
    }
    
    fun cerrarDetallesFecha() {
        _uiState.update { 
            it.copy(
                mostrarDetallesFecha = false,
                fechaSeleccionada = null,
                ausentesEnFechaSeleccionada = emptyList()
            ) 
        }
    }
    
    fun cambiarMes(incremento: Int) {
        viewModelScope.launch {
            try {
                var periodoActual = _uiState.value.periodoActual
                // Usar obtenerPeriodoSiguiente/Anterior para respetar período 26-25
                repeat(kotlin.math.abs(incremento)) {
                    periodoActual = if (incremento > 0) {
                        periodoActual.obtenerPeriodoSiguiente()
                    } else {
                        periodoActual.obtenerPeriodoAnterior()
                    }
                }
                val nuevoPeriodo = periodoActual
                
                _uiState.update { it.copy(periodoActual = nuevoPeriodo) }
                
                // Recargar datos del nuevo mes
                cargarCalendario()
                
            } catch (e: Exception) {
                Log.e("AusenciasVM", "Error cambiando mes", e)
            }
        }
    }
    
    fun crearAusencia(
        legajo: String,
        nombreEmpleado: String,
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        motivo: String?,
        observaciones: String? = null,
        esJustificada: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                Log.d("AusenciasVM", "╔═══ CREANDO AUSENCIA ═══╗")
                Log.d("AusenciasVM", "Empleado: $nombreEmpleado")
                Log.d("AusenciasVM", "Legajo (employee_id): $legajo")
                Log.d("AusenciasVM", "Fechas: $fechaInicio - $fechaFin")
                Log.d("AusenciasVM", "Motivo: $motivo | Obs: $observaciones | Justificada: $esJustificada")
                
                val ausencia = Ausencia(
                    legajoEmpleado = legajo,
                    nombreEmpleado = nombreEmpleado,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    motivo = motivo,
                    observaciones = observaciones,
                    esJustificada = esJustificada
                )
                
                // 1. Guardar en Room (local first)
                crearAusenciaUseCase(ausencia)
                Log.d("AusenciasVM", "✅ Ausencia guardada en BD local (Room)")

                // 2. Enviar al API de producción (en un bloque try/catch separado para no bloquear)
                try {
                    Log.d("AusenciasVM_API", "╔═══ ENVIANDO AL API ═══╗")
                    Log.d("AusenciasVM_API", "  employee_id: $legajo")
                    Log.d("AusenciasVM_API", "  start_date: $fechaInicio | end_date: $fechaFin")
                    Log.d("AusenciasVM_API", "  reason: ${motivo ?: "Sin motivo"} | is_justified: $esJustificada")

                    // CRÍTICO: asegurar token antes de llamar al API
                    val tieneToken = deviceIdentityManager.ensureDeviceToken()
                    Log.d("AusenciasVM_API", "  ensureDeviceToken() → tieneToken=$tieneToken")

                    if (!tieneToken) {
                        Log.w("AusenciasVM_API", "  ⚠️ Sin device_token. Ausencia guardada en Room, se enviará al API en el próximo Cierre de Tarja.")
                        Log.d("AusenciasVM_API", "╚═════════════════════╝")
                    } else {
                        val request = CreateAbsenceRequestDto(
                            employeeId = legajo,
                            startDate = fechaInicio.toString(),
                            endDate = fechaFin.toString(),
                            reason = motivo ?: "Ausencia",
                            observations = observaciones,
                            isJustified = esJustificada
                        )

                        val response = ausenciasApiService.createAbsence(request)
                        Log.d("AusenciasVM_API", "  HTTP ${response.code()}")

                        if (response.isSuccessful) {
                            Log.d("AusenciasVM_API", "  ✅ Ausencia enviada exitosamente al API")
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "sin cuerpo"
                            Log.w("AusenciasVM_API", "  ⚠️ API respondió ${response.code()}: $errorBody (ausencia guardada localmente de igual forma)")
                        }
                        Log.d("AusenciasVM_API", "╚═════════════════════╝")
                    }
                } catch (apiEx: Exception) {
                    // IMPORTANTE: el fallo del API NO bloquea el flujo. La ausencia queda en Room.
                    Log.e("AusenciasVM_API", "❌ Error enviando ausencia al API (NO bloquea): ${apiEx.javaClass.simpleName} - ${apiEx.message}", apiEx)
                }
                
                // Recargar calendario para mostrar los cambios
                hasLoadedInitialData = false
                cargarCalendario()
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        mostrarDialogoAgregar = false,
                        mensaje = "✅ Ausencia registrada exitosamente",
                        ausenciaCreada = true
                    ) 
                }
                
                Log.d("AusenciasVM", "╚═══ AUSENCIA CREADA EXITOSAMENTE ═══╝")
                
                // NOTIFICAR A OTRAS PANTALLAS QUE SE CRED UNA AUSENCIA
                notificarAusenciaCreada()
                
            } catch (e: Exception) {
                Log.e("AusenciasVM", "Error creando ausencia", e)
                _uiState.update { it.copy(error = "Error al crear ausencia: ${e.message}") }
            }
        }
    }
    
    /**
     * Abrir diálogo de edición de ausencia
     */
    fun abrirDialogoEditarAusencia(ausencia: Ausencia) {
        Log.d("AusenciasVM", "Abriendo diálogo para editar ausencia ID: ${ausencia.id}")
        _uiState.update { 
            it.copy(
                ausenciaParaEditar = ausencia,
                mostrarDialogoEditar = true,
                mostrarDetallesFecha = false
            ) 
        }
    }
    
    /**
     * Cerrar diálogo de edición
     */
    fun cerrarDialogoEditar() {
        _uiState.update { 
            it.copy(
                mostrarDialogoEditar = false,
                ausenciaParaEditar = null
            ) 
        }
    }
    
    /**
     * Actualizar ausencia existente
     */
    fun actualizarAusencia(
        ausenciaId: Long,
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        motivo: String?
    ) {
        viewModelScope.launch {
            try {
                Log.d("AusenciasVM", "═══ ACTUALIZANDO AUSENCIA ═══")
                Log.d("AusenciasVM", "ID: $ausenciaId")
                Log.d("AusenciasVM", "Nuevas fechas: $fechaInicio - $fechaFin")
                Log.d("AusenciasVM", "Nuevo motivo: $motivo")
                
                val ausenciaOriginal = _uiState.value.ausenciaParaEditar
                if (ausenciaOriginal == null) {
                    Log.e("AusenciasVM", "No hay ausencia para editar")
                    return@launch
                }
                
                val ausenciaActualizada = ausenciaOriginal.copy(
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    motivo = motivo
                )
                
                actualizarAusenciaUseCase(ausenciaActualizada)
                
                Log.d("AusenciasVM", "✅ Ausencia actualizada en BD")
                
                // Recargar calendario
                hasLoadedInitialData = false
                cargarCalendario()
                
                _uiState.update { 
                    it.copy(
                        mostrarDialogoEditar = false,
                        ausenciaParaEditar = null,
                        mensaje = "✅ Ausencia actualizada exitosamente"
                    ) 
                }
                
                Log.d("AusenciasVM", "═══ AUSENCIA ACTUALIZADA EXITOSAMENTE ═══")
                
            } catch (e: Exception) {
                Log.e("AusenciasVM", "Error actualizando ausencia", e)
                _uiState.update { it.copy(error = "Error al actualizar ausencia: ${e.message}") }
            }
        }
    }
    
    /**
     * Solicitar confirmación para eliminar ausencia
     */
    fun solicitarEliminarAusencia(ausencia: Ausencia) {
        Log.d("AusenciasVM", "Solicitando confirmación para eliminar ausencia ID: ${ausencia.id}")
        _uiState.update { 
            it.copy(
                ausenciaParaEliminar = ausencia,
                mostrarConfirmacionEliminar = true,
                mostrarDetallesFecha = false
            ) 
        }
    }
    
    /**
     * Cancelar eliminación de ausencia
     */
    fun cancelarEliminarAusencia() {
        _uiState.update { 
            it.copy(
                mostrarConfirmacionEliminar = false,
                ausenciaParaEliminar = null
            ) 
        }
    }
    
    /**
     * Confirmar y eliminar ausencia
     */
    fun confirmarEliminarAusencia() {
        val ausencia = _uiState.value.ausenciaParaEliminar ?: return
        
        viewModelScope.launch {
            try {
                Log.d("AusenciasVM", "═══ ELIMINANDO AUSENCIA ═══")
                Log.d("AusenciasVM", "ID: ${ausencia.id}")
                Log.d("AusenciasVM", "Empleado: ${ausencia.nombreEmpleado}")
                
                eliminarAusenciaUseCase(ausencia)
                
                Log.d("AusenciasVM", "✅ Ausencia eliminada de BD")
                
                // Recargar calendario
                hasLoadedInitialData = false
                cargarCalendario()
                
                _uiState.update { 
                    it.copy(
                        mostrarConfirmacionEliminar = false,
                        ausenciaParaEliminar = null,
                        mensaje = "✅ Ausencia eliminada exitosamente"
                    ) 
                }
                
                Log.d("AusenciasVM", "═══ AUSENCIA ELIMINADA EXITOSAMENTE ═══")
                
            } catch (e: Exception) {
                Log.e("AusenciasVM", "Error eliminando ausencia", e)
                _uiState.update { it.copy(error = "Error al eliminar ausencia: ${e.message}") }
            }
        }
    }
    
    /**
     * Función para refrescar manualmente el calendario
     */
    fun refrescarCalendario() {
        if (!isLoading) {
            Log.d("AusenciasVM", "Refrescando calendario manualmente...")
            hasLoadedInitialData = false
            cargarCalendario()
        } else {
            Log.d("AusenciasVM", "Refresco ignorado - carga en progreso")
        }
    }
    
    fun clearMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
    
    fun clearAusenciaCreada() {
        _uiState.update { it.copy(ausenciaCreada = false) }
    }
    
    private fun notificarAusenciaCreada() {
        Log.d("AusenciasVM", "📢 NOTIFICANDO: Ausencia creada - otras pantallas deben actualizarse")
        // Aquí podríamos usar un EventBus o SharedFlow para notificar a otras pantallas
        // Por ahora, solo loggeamos
    }
}
