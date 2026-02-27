package com.registro.empleados.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.model.RegistroAsistencia
import com.registro.empleados.domain.usecase.asistencia.GetRegistrosByRangoUseCase
import com.registro.empleados.domain.usecase.empleado.GetAllEmpleadosActivosUseCase
import com.registro.empleados.domain.usecase.export.ExportarRegistrosAsistenciaUseCase
import com.registro.empleados.domain.usecase.database.LimpiarBaseDatosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel para la pantalla de reportes y exportación.
 * Maneja el estado de la UI y las operaciones de generación de reportes.
 */
@HiltViewModel
class ReportesViewModel @Inject constructor(
    private val getRegistrosByRangoUseCase: GetRegistrosByRangoUseCase,
    private val getAllEmpleadosActivosUseCase: GetAllEmpleadosActivosUseCase,
    private val exportarRegistrosAsistenciaUseCase: ExportarRegistrosAsistenciaUseCase,
    private val limpiarBaseDatosUseCase: LimpiarBaseDatosUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportesUiState())
    val uiState: StateFlow<ReportesUiState> = _uiState.asStateFlow()
    
    private var yaInicializado = false

    init {
        android.util.Log.d("ReportesVM", "INIT - ReportesViewModel inicializado")
        if (!yaInicializado) {
            android.util.Log.d("ReportesVM", "INIT - Primera vez, cargando datos...")
            yaInicializado = true
            loadEmpleados()
            cargarConfiguracion()
        } else {
            android.util.Log.d("ReportesVM", "INIT - Ya inicializado, saltando carga")
        }
    }
    
    /**
     * Carga la configuración del encargado y sector.
     */
    private fun cargarConfiguracion() {
        viewModelScope.launch {
            val nombreEncargado = appPreferences.getNombreEncargado()
            val sectorActual = appPreferences.getSectorSeleccionado()
            
            android.util.Log.d("ReportesVM", "Encargado: $nombreEncargado")
            android.util.Log.d("ReportesVM", "Sector: $sectorActual")
            
            _uiState.update { state ->
                val empleadosDelSector = if (sectorActual.isNotBlank()) {
                    state.empleados.filter { empleado ->
                        empleado.sector.equals(sectorActual, ignoreCase = true)
                    }
                } else {
                    state.empleados
                }
                
                state.copy(
                    nombreEncargado = nombreEncargado,
                    sectorActual = sectorActual,
                    empleadosDelSector = empleadosDelSector
                ) 
            }
        }
    }

    /**
     * Carga todos los empleados activos para los filtros.
     */
    private fun loadEmpleados() {
        viewModelScope.launch {
            try {
                getAllEmpleadosActivosUseCase().collect { todosLosEmpleados ->
                    val sectorActual = _uiState.value.sectorActual
                    val empleadosDelSector = if (sectorActual.isNotBlank()) {
                        todosLosEmpleados.filter { empleado ->
                            empleado.sector.equals(sectorActual, ignoreCase = true)
                        }
                    } else {
                        todosLosEmpleados
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        empleados = todosLosEmpleados,
                        empleadosDelSector = empleadosDelSector,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al cargar empleados"
                )
            }
        }
    }

    /**
     * Actualiza la fecha de inicio del rango de reportes.
     */
    fun onFechaInicioChanged(fecha: LocalDate) {
        _uiState.value = _uiState.value.copy(
            fechaInicio = fecha,
            error = null
        )
    }

    /**
     * Actualiza la fecha de fin del rango de reportes.
     */
    fun onFechaFinChanged(fecha: LocalDate) {
        _uiState.value = _uiState.value.copy(
            fechaFin = fecha,
            error = null
        )
    }

    /**
     * Actualiza el nombre del encargado del sector.
     */
    fun onEncargadoChanged(nombre: String) {
        _uiState.update { it.copy(nombreEncargado = nombre) }
    }

    /**
     * Actualiza el filtro de legajo.
     */
    fun onLegajoFiltroChanged(legajo: String) {
        _uiState.value = _uiState.value.copy(
            legajoFiltro = legajo,
            error = null
        )
    }

    /**
     * Genera el reporte con los parámetros seleccionados.
     */
    fun generarReporte() {
        val state = _uiState.value
        
        if (!esRangoValido(state.fechaInicio, state.fechaFin)) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            try {
                // Obtener todos los registros del rango
                val todosLosRegistros = getRegistrosByRangoUseCase(
                    fechaInicio = state.fechaInicio,
                    fechaFin = state.fechaFin,
                    legajo = state.legajoFiltro.takeIf { it.isNotBlank() }
                )
                
                // Filtrar registros por sector: solo los de empleados del sector seleccionado
                val legajosDelSector = state.empleadosDelSector.mapNotNull { empleado ->
                    empleado.legajo ?: "SIN_LEGAJO_${empleado.nombreCompleto.hashCode()}"
                }.toSet()
                
                val registrosFiltrados = if (state.sectorActual.isNotBlank()) {
                    todosLosRegistros.filter { registro ->
                        legajosDelSector.contains(registro.legajoEmpleado)
                    }
                } else {
                    todosLosRegistros
                }
                
                android.util.Log.d("ReportesVM", "Total registros: ${todosLosRegistros.size}")
                android.util.Log.d("ReportesVM", "Registros del sector '${state.sectorActual}': ${registrosFiltrados.size}")
                
                _uiState.value = _uiState.value.copy(
                    registrosReporte = registrosFiltrados,
                    isLoading = false,
                    reporteGenerado = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al generar reporte",
                    isLoading = false
                )
            }
        }
    }

    /**
     * Valida el rango de fechas seleccionado.
     */
    private fun esRangoValido(fechaInicio: LocalDate, fechaFin: LocalDate): Boolean {
        if (fechaInicio.isAfter(fechaFin)) {
            _uiState.value = _uiState.value.copy(
                error = "La fecha de inicio no puede ser posterior a la fecha de fin"
            )
            return false
        }
        
        val diasDiferencia = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin)
        if (diasDiferencia > 365) {
            _uiState.value = _uiState.value.copy(
                error = "El rango de fechas no puede ser mayor a un año"
            )
            return false
        }
        
        return true
    }

    /**
     * Prepara los datos para exportación.
     */
    fun prepararExportacion() {
        val state = _uiState.value
        if (state.registrosReporte.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                error = "No hay datos para exportar. Genere un reporte primero."
            )
            return
        }
        
        _uiState.value = _uiState.value.copy(
            mostrarOpcionesExportacion = true
        )
    }

    /**
     * Inicia la exportación a Excel.
     */
    fun exportarAExcel() {
        ejecutarExportacion(ExportarRegistrosAsistenciaUseCase.FormatoExportacion.EXCEL)
    }

    /**
     * Inicia la exportación a CSV.
     */
    fun exportarACSV() {
        ejecutarExportacion(ExportarRegistrosAsistenciaUseCase.FormatoExportacion.CSV)
    }

    /**
     * Lógica común de exportación (Excel o CSV). Evita duplicación.
     */
    private fun ejecutarExportacion(formato: ExportarRegistrosAsistenciaUseCase.FormatoExportacion) {
        val isExcel = formato == ExportarRegistrosAsistenciaUseCase.FormatoExportacion.EXCEL
        val nombreFormato = if (isExcel) "Excel" else "CSV"
        _uiState.value = _uiState.value.copy(
            exportandoExcel = isExcel,
            exportandoCSV = !isExcel,
            error = null,
            mensaje = null
        )

        viewModelScope.launch {
            try {
                val resultado = exportarRegistrosAsistenciaUseCase(
                    fechaInicio = _uiState.value.fechaInicio,
                    fechaFin = _uiState.value.fechaFin,
                    formato = formato,
                    nombreEncargado = _uiState.value.nombreEncargado,
                    nombreSector = _uiState.value.sectorActual ?: ""
                )

                _uiState.value = _uiState.value.copy(
                    exportandoExcel = false,
                    exportandoCSV = false,
                    mensaje = resultado.mensaje,
                    mostrarOpcionesExportacion = false,
                    archivoParaCompartir = if (resultado.exito) resultado.archivosGenerados.firstOrNull() else null
                )

                if (resultado.exito) {
                    kotlinx.coroutines.delay(100)
                    _uiState.value = _uiState.value.copy(archivoParaCompartir = null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    exportandoExcel = false,
                    exportandoCSV = false,
                    error = "Error al exportar a $nombreFormato: ${e.message ?: "Error desconocido"}"
                )
            }
        }
    }
    
    /**
     * Exporta y comparte archivo (Excel o CSV).
     */
    fun exportarYCompartir(formato: String, context: android.content.Context) {
        val state = _uiState.value
        val formatoExportacion = if (formato.lowercase() == "excel") {
            ExportarRegistrosAsistenciaUseCase.FormatoExportacion.EXCEL
        } else {
            ExportarRegistrosAsistenciaUseCase.FormatoExportacion.CSV
        }
        
        _uiState.value = _uiState.value.copy(
            exportandoExcel = formato.lowercase() == "excel",
            exportandoCSV = formato.lowercase() == "csv",
            error = null
        )
        
        viewModelScope.launch {
            try {
                val resultado = exportarRegistrosAsistenciaUseCase(
                    fechaInicio = state.fechaInicio,
                    fechaFin = state.fechaFin,
                    formato = formatoExportacion
                )
                
                if (resultado.exito) {
                    val rutaArchivo = resultado.mensaje.substringAfter("guardado en: ")
                    val archivo = java.io.File(rutaArchivo)
                    
                    if (archivo.exists()) {
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            archivo
                        )
                        
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = when (formato.lowercase()) {
                                "excel" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                "csv" -> "text/csv"
                                else -> "application/octet-stream"
                            }
                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Compartir reporte"))
                        
                        _uiState.value = _uiState.value.copy(
                            exportandoExcel = false,
                            exportandoCSV = false,
                            mensaje = "Archivo compartido exitosamente",
                            mostrarOpcionesExportacion = false,
                            // Resetear para que el cartel no permanezca visible al volver
                            archivoParaCompartir = null
                        )
                    } else {
                        throw Exception("El archivo no se encontró en la ruta especificada")
                    }
                } else {
                    throw Exception(resultado.mensaje)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    exportandoExcel = false,
                    exportandoCSV = false,
                    error = e.message ?: "Error al exportar y compartir archivo"
                )
            }
        }
    }

    /**
     * Cierra las opciones de exportación.
     */
    fun cerrarOpcionesExportacion() {
        _uiState.value = _uiState.value.copy(
            mostrarOpcionesExportacion = false
        )
    }

    /**
     * Actualiza las fechas de inicio y fin.
     */
    fun actualizarFechas(fechaInicio: LocalDate, fechaFin: LocalDate) {
        _uiState.value = _uiState.value.copy(
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )
    }

    /**
     * Limpia los mensajes de error y éxito.
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            mensaje = null
        )
    }

    /**
     * Reinicia el reporte.
     */
    fun reiniciarReporte() {
        _uiState.value = _uiState.value.copy(
            registrosReporte = emptyList(),
            reporteGenerado = false,
            mostrarOpcionesExportacion = false,
            error = null,
            mensaje = null
        )
    }

    /**
     * Limpia completamente la base de datos.
     * Elimina todos los empleados y registros de asistencia.
     */
    fun limpiarBaseDatos() {
        android.util.Log.d("ReportesViewModel", "=== INICIANDO LIMPIEZA DE BASE DE DATOS ===")
        
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null,
            mensaje = null
        )
        
        viewModelScope.launch {
            try {
                android.util.Log.d("ReportesViewModel", "Llamando a limpiarBaseDatosUseCase...")
                
                val resultado = limpiarBaseDatosUseCase()
                
                android.util.Log.d("ReportesViewModel", "Resultado limpieza - Exito: ${resultado.exito}")
                android.util.Log.d("ReportesViewModel", "Resultado limpieza - Mensaje: ${resultado.mensaje}")
                
                if (resultado.exito) {
                    android.util.Log.d("ReportesViewModel", "Base de datos limpiada exitosamente")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mensaje = resultado.mensaje
                    )
                    
                    // Recargar empleados después de limpiar
                    loadEmpleados()
                } else {
                    android.util.Log.e("ReportesViewModel", "Error en limpieza: ${resultado.mensaje}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = resultado.mensaje
                    )
                }
                
                android.util.Log.d("ReportesViewModel", "=== LIMPIEZA DE BASE DE DATOS COMPLETADA ===")
                
            } catch (e: Exception) {
                android.util.Log.e("ReportesViewModel", "ERROR al limpiar base de datos", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al limpiar base de datos: ${e.message ?: "Error desconocido"}"
                )
            }
        }
    }
    
}

/**
 * Estado de la UI para la pantalla de reportes.
 */
data class ReportesUiState(
    val empleados: List<Empleado> = emptyList(),
    val empleadosDelSector: List<Empleado> = emptyList(),
    val fechaInicio: LocalDate = com.registro.empleados.domain.model.PeriodoLaboral.calcularPeriodoActual().fechaInicio,
    val fechaFin: LocalDate = com.registro.empleados.domain.model.PeriodoLaboral.calcularPeriodoActual().fechaFin,
    val legajoFiltro: String = "",
    val nombreEncargado: String = "",
    val sectorActual: String = "",
    val registrosReporte: List<RegistroAsistencia> = emptyList(),
    val reporteGenerado: Boolean = false,
    val mostrarOpcionesExportacion: Boolean = false,
    val isLoading: Boolean = false,
    val exportandoExcel: Boolean = false,
    val exportandoCSV: Boolean = false,
    val archivoParaCompartir: String? = null,
    val error: String? = null,
    val mensaje: String? = null
) {
    /**
     * Verifica si se puede generar un reporte.
     */
    val puedeGenerarReporte: Boolean
        get() = !fechaInicio.isAfter(fechaFin)

    /**
     * Obtiene el nombre del empleado seleccionado para el filtro.
     */
    fun getNombreEmpleadoFiltro(empleados: List<Empleado>): String {
        return if (legajoFiltro.isBlank()) {
            "Todos los empleados"
        } else {
            empleados.find { it.legajo == legajoFiltro }?.nombreCompleto ?: "Empleado no encontrado"
        }
    }

    /**
     * Calcula estadísticas del reporte.
     */
    val estadisticas: EstadisticasReporte
        get() = EstadisticasReporte.calcular(registrosReporte)
}

/**
 * Estadísticas calculadas del reporte.
 */
data class EstadisticasReporte(
    val totalRegistros: Int,
    val empleadosUnicos: Int,
    val totalHorasTrabajadas: Double,
    val promedioHorasPorDia: Double
) {
    companion object {
        fun calcular(registros: List<RegistroAsistencia>): EstadisticasReporte {
            val totalRegistros = registros.size
            val empleadosUnicos = registros.map { it.legajoEmpleado }.distinct().size
            val totalHorasTrabajadas = registros.sumOf { it.horasTrabajadas.toDouble() }
            val promedioHorasPorDia = if (totalRegistros > 0) totalHorasTrabajadas / totalRegistros else 0.0
            
            return EstadisticasReporte(
                totalRegistros = totalRegistros,
                empleadosUnicos = empleadosUnicos,
                totalHorasTrabajadas = totalHorasTrabajadas,
                promedioHorasPorDia = promedioHorasPorDia
            )
        }
    }

}

