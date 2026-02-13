package com.registro.empleados.domain.usecase.export

import android.util.Log
import com.registro.empleados.data.export.CsvExportService
import com.registro.empleados.data.export.ExcelExportService
import com.registro.empleados.domain.model.Ausencia
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.model.RegistroAsistencia
import com.registro.empleados.domain.repository.AusenciaRepository
import com.registro.empleados.domain.repository.EmpleadoRepository
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import javax.inject.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first

/**
 * Caso de uso para exportar registros de asistencia.
 * Maneja la exportación tanto en formato Excel como CSV.
 */
class ExportarRegistrosAsistenciaUseCase @Inject constructor(
    private val excelExportService: ExcelExportService,
    private val csvExportService: CsvExportService,
    private val empleadoRepository: EmpleadoRepository,
    private val registroAsistenciaRepository: RegistroAsistenciaRepository,
    private val ausenciaRepository: AusenciaRepository
) {
    
    /**
     * Formato de exportación disponible.
     */
    enum class FormatoExportacion {
        EXCEL, CSV, AMBOS
    }
    
    /**
     * Resultado de la exportación.
     */
    data class ResultadoExportacion(
        val exito: Boolean,
        val archivosGenerados: List<String>,
        val mensaje: String
    )
    
    /**
     * Exporta registros de asistencia en el formato especificado.
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @param formato Formato de exportación (Excel, CSV o ambos)
     * @param nombreEncargado Nombre del encargado del sector
     * @param nombreSector Nombre del sector para el nombre del archivo
     * @return Resultado de la exportación
     */
    suspend operator fun invoke(
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        formato: FormatoExportacion = FormatoExportacion.EXCEL,
        nombreEncargado: String = "",
        nombreSector: String = ""
    ): ResultadoExportacion {
        Log.d("ExportarRegistrosUseCase", "=== INICIANDO EXPORTACIÓN ===")
        Log.d("ExportarRegistrosUseCase", "Fechas: $fechaInicio a $fechaFin")
        Log.d("ExportarRegistrosUseCase", "Formato: $formato")
        
        return try {
            // Obtener registros del período
            Log.d("ExportarRegistrosUseCase", "Obteniendo registros del período...")
            val fechaInicioStr = fechaInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val fechaFinStr = fechaFin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            
            Log.d("ExportarRegistrosUseCase", "Consultando registros de $fechaInicioStr a $fechaFinStr")
            val registros = registroAsistenciaRepository.getRegistrosByRango(fechaInicioStr, fechaFinStr)
            
            Log.d("ExportarRegistrosUseCase", "Registros obtenidos: ${registros.size}")
            
            if (registros.isEmpty()) {
                Log.w("ExportarRegistrosUseCase", "No hay registros en el período especificado")
                return ResultadoExportacion(
                    exito = false,
                    archivosGenerados = emptyList(),
                    mensaje = "No hay registros de asistencia en el período especificado"
                )
            }
            
            // Obtener empleados para incluir nombres (todos, incluso inactivos con registros históricos)
            Log.d("ExportarRegistrosUseCase", "Obteniendo todos los empleados...")
            val empleados = empleadoRepository.getAllEmpleados().first()
            Log.d("ExportarRegistrosUseCase", "Empleados obtenidos: ${empleados.size}")
            
            // Obtener ausencias del período
            Log.d("ExportarRegistrosUseCase", "Obteniendo ausencias del período...")
            val ausencias = ausenciaRepository.getAusenciasByRango(fechaInicio, fechaFin)
            Log.d("ExportarRegistrosUseCase", "Ausencias obtenidas: ${ausencias.size}")
            
            val archivosGenerados = mutableListOf<String>()
            
            when (formato) {
                FormatoExportacion.EXCEL -> {
                    Log.d("ExportarRegistrosUseCase", "Exportando a Excel...")
                    val archivo = excelExportService.exportarRegistrosAsistencia(
                        registros, empleados, fechaInicio, fechaFin, nombreEncargado, ausencias, nombreSector
                    )
                    Log.d("ExportarRegistrosUseCase", "Archivo Excel generado: $archivo")
                    archivosGenerados.add(archivo)
                }
                FormatoExportacion.CSV -> {
                    Log.d("ExportarRegistrosUseCase", "Exportando a CSV...")
                    val archivo = csvExportService.exportarRegistrosAsistencia(
                        registros, empleados, fechaInicio, fechaFin, nombreEncargado, ausencias, nombreSector
                    )
                    Log.d("ExportarRegistrosUseCase", "Archivo CSV generado: $archivo")
                    archivosGenerados.add(archivo)
                }
                FormatoExportacion.AMBOS -> {
                    Log.d("ExportarRegistrosUseCase", "Exportando a ambos formatos...")
                    val archivoExcel = excelExportService.exportarRegistrosAsistencia(
                        registros, empleados, fechaInicio, fechaFin, nombreEncargado, ausencias, nombreSector
                    )
                    val archivoCsv = csvExportService.exportarRegistrosAsistencia(
                        registros, empleados, fechaInicio, fechaFin, nombreEncargado, ausencias, nombreSector
                    )
                    Log.d("ExportarRegistrosUseCase", "Archivo Excel: $archivoExcel")
                    Log.d("ExportarRegistrosUseCase", "Archivo CSV: $archivoCsv")
                    archivosGenerados.addAll(listOf(archivoExcel, archivoCsv))
                }
            }
            
            Log.d("ExportarRegistrosUseCase", "=== EXPORTACIÓN COMPLETADA EXITOSAMENTE ===")
            Log.d("ExportarRegistrosUseCase", "Archivos generados: ${archivosGenerados.size}")
            Log.d("ExportarRegistrosUseCase", "Rutas: $archivosGenerados")
            
            ResultadoExportacion(
                exito = true,
                archivosGenerados = archivosGenerados,
                mensaje = "Se exportaron ${registros.size} registros en ${archivosGenerados.size} archivo(s)"
            )
        } catch (e: Exception) {
            Log.e("ExportarRegistrosUseCase", "ERROR en exportación", e)
            ResultadoExportacion(
                exito = false,
                archivosGenerados = emptyList(),
                mensaje = "Error al exportar registros: ${e.message}"
            )
        }
    }
    
    /**
     * Exporta registros de asistencia para un empleado específico.
     * @param legajoEmpleado Legajo del empleado
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @param formato Formato de exportación
     * @return Resultado de la exportación
     */
    suspend fun exportarPorEmpleado(
        legajoEmpleado: String,
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        formato: FormatoExportacion = FormatoExportacion.EXCEL
    ): ResultadoExportacion {
        return try {
            // Obtener registros del empleado en el período
            val todosLosRegistros = registroAsistenciaRepository.getRegistrosByRango(
                fechaInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 
                fechaFin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )
            val registros = todosLosRegistros.filter { it.legajoEmpleado == legajoEmpleado }
            
            if (registros.isEmpty()) {
                return ResultadoExportacion(
                    exito = false,
                    archivosGenerados = emptyList(),
                    mensaje = "No hay registros de asistencia para el empleado $legajoEmpleado en el período especificado"
                )
            }
            
            // Obtener empleados para incluir nombres
            val empleados = empleadoRepository.getAllEmpleadosActivos().first()
            
            val archivosGenerados = mutableListOf<String>()
            
            when (formato) {
                FormatoExportacion.EXCEL -> {
                    val archivo = excelExportService.exportarRegistrosAsistencia(
                        registros, empleados, fechaInicio, fechaFin
                    )
                    archivosGenerados.add(archivo)
                }
                FormatoExportacion.CSV -> {
                    val archivo = csvExportService.exportarRegistrosAsistencia(
                        registros, empleados, fechaInicio, fechaFin
                    )
                    archivosGenerados.add(archivo)
                }
                FormatoExportacion.AMBOS -> {
                    val archivoExcel = excelExportService.exportarRegistrosAsistencia(
                        registros, empleados, fechaInicio, fechaFin
                    )
                    val archivoCsv = csvExportService.exportarRegistrosAsistencia(
                        registros, empleados, fechaInicio, fechaFin
                    )
                    archivosGenerados.addAll(listOf(archivoExcel, archivoCsv))
                }
            }
            
            ResultadoExportacion(
                exito = true,
                archivosGenerados = archivosGenerados,
                mensaje = "Se exportaron ${registros.size} registros del empleado $legajoEmpleado en ${archivosGenerados.size} archivo(s)"
            )
        } catch (e: Exception) {
            ResultadoExportacion(
                exito = false,
                archivosGenerados = emptyList(),
                mensaje = "Error al exportar registros del empleado: ${e.message}"
            )
        }
    }
}
