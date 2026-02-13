package com.registro.empleados.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.domain.model.DiaLaboral
import com.registro.empleados.domain.model.PeriodoLaboral
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import com.registro.empleados.domain.usecase.calendario.GetDiasLaboralesUseCase
import com.registro.empleados.domain.usecase.calendario.GetPeriodoActualUseCase
import com.registro.empleados.domain.usecase.calendario.GenerarDiasLaboralesBasicosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel para la pantalla de calendario.
 * Maneja el estado de la UI y las operaciones del calendario personalizado 26-25.
 */
@HiltViewModel
class CalendarioViewModel @Inject constructor(
    private val getDiasLaboralesUseCase: GetDiasLaboralesUseCase,
    private val getPeriodoActualUseCase: GetPeriodoActualUseCase,
    private val generarDiasLaboralesBasicosUseCase: GenerarDiasLaboralesBasicosUseCase,
    private val registroRepository: RegistroAsistenciaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarioUiState(
        diasLaborales = emptyList(),
        periodoActual = PeriodoLaboral(
            fechaInicio = LocalDate.of(2025, 9, 26),
            fechaFin = LocalDate.of(2025, 10, 25)
        ),
        isLoading = true,
        error = null
    ))
    val uiState: StateFlow<CalendarioUiState> = _uiState.asStateFlow()

    init {
        android.util.Log.d("CalendarioVM", "ViewModel inicializado")
        
        // CARGAR INMEDIATAMENTE
        cargarCalendario()
    }

    private fun cargarCalendario() {
        viewModelScope.launch {
            try {
                android.util.Log.d("CalendarioVM", "Cargando calendario...")
                
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // PASO 1: Obtener período - CON FALLBACK
                val periodo = try {
                    getPeriodoActualUseCase()
                } catch (e: Exception) {
                    android.util.Log.w("CalendarioVM", "Error obteniendo período, usando fallback", e)
                    PeriodoLaboral(
                        fechaInicio = LocalDate.of(2025, 9, 26),
                        fechaFin = LocalDate.of(2025, 10, 25)
                    )
                }
                
                // PASO 2: Obtener días - CON FALLBACK
                var dias = try {
                    getDiasLaboralesUseCase(periodo.fechaInicio, periodo.fechaFin)
                } catch (e: Exception) {
                    android.util.Log.w("CalendarioVM", "Error consultando días", e)
                    emptyList()
                }
                
                // PASO 3: Si no hay días, generar
                if (dias.isEmpty()) {
                    try {
                        generarDiasLaboralesBasicosUseCase(
                            periodo.fechaInicio.year,
                            periodo.fechaInicio.monthValue
                        )
                        
                        if (periodo.fechaFin.monthValue != periodo.fechaInicio.monthValue) {
                            generarDiasLaboralesBasicosUseCase(
                                periodo.fechaFin.year,
                                periodo.fechaFin.monthValue
                            )
                        }
                        
                        delay(300)
                        dias = getDiasLaboralesUseCase(periodo.fechaInicio, periodo.fechaFin)
                        
                    } catch (e: Exception) {
                        android.util.Log.w("CalendarioVM", "Error generando días", e)
                    }
                }
                
                // PASO 4: Actualizar estado - SIEMPRE
                _uiState.update {
                    it.copy(
                        diasLaborales = dias,
                        periodoActual = periodo,
                        isLoading = false,
                        error = if (dias.isEmpty()) "No se pudieron cargar los días del calendario" else null
                    )
                }
                
                android.util.Log.d("CalendarioVM", "Estado actualizado: ${dias.size} días")
                
            } catch (e: Exception) {
                android.util.Log.e("CalendarioVM", "Error crítico en cargarCalendario()", e)
                
                // ACTUALIZAR CON ERROR - NO DEJAR EL ESTADO COLGADO
                _uiState.update {
                    it.copy(
                        diasLaborales = emptyList(),
                        isLoading = false,
                        error = "Error crítico: ${e.message}"
                    )
                }
            }
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
                
                var dias = try {
                    getDiasLaboralesUseCase(nuevoPeriodo.fechaInicio, nuevoPeriodo.fechaFin)
                } catch (e: Exception) {
                    android.util.Log.e("CalendarioVM", "Error obteniendo días del nuevo mes", e)
                    emptyList()
                }
                
                if (dias.isEmpty()) {
                    try {
                        generarDiasLaboralesBasicosUseCase(
                            nuevoPeriodo.fechaInicio.year,
                            nuevoPeriodo.fechaInicio.monthValue
                        )
                        delay(200)
                        dias = getDiasLaboralesUseCase(nuevoPeriodo.fechaInicio, nuevoPeriodo.fechaFin)
                    } catch (e: Exception) {
                        android.util.Log.e("CalendarioVM", "Error generando días", e)
                    }
                }
                
                _uiState.update {
                    it.copy(
                        diasLaborales = dias,
                        periodoActual = nuevoPeriodo
                    )
                }
                
            } catch (e: Exception) {
                android.util.Log.e("CalendarioVM", "Error en cambiarMes()", e)
                _uiState.update { it.copy(error = "Error al cambiar mes: ${e.message}") }
            }
        }
    }

    fun recargarCalendario() {
        android.util.Log.d("CalendarioVM", "Recargando calendario...")
        cargarCalendario()
    }
}

/**
 * Estado de la UI para la pantalla de calendario.
 */
data class CalendarioUiState(
    val diasLaborales: List<DiaLaboral> = emptyList(),
    val periodoActual: PeriodoLaboral = PeriodoLaboral(
        fechaInicio = LocalDate.of(2025, 9, 26),
        fechaFin = LocalDate.of(2025, 10, 25)
    ),
    val isLoading: Boolean = true,
    val error: String? = null
)