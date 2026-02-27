package com.registro.empleados.data.export

import android.content.Context
import com.registro.empleados.domain.model.Ausencia
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.model.RegistroAsistencia
import jxl.Workbook
import jxl.write.WritableWorkbook
import jxl.write.WritableSheet
import jxl.write.Label
import jxl.write.Number
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

    /**
     * Servicio para exportar datos a formato Excel (.xls) con formato de tabla de asistencia.
     * Formato: N, DNI, RUTA 5 (nombre), días del período (21-31, 1-20), HORAS (total)
     */
@Singleton
class ExcelExportService @Inject constructor(
    private val context: Context
) {
    
    /**
     * Exporta registros de asistencia a un archivo Excel con formato de tabla de asistencia.
     * Formato: ENCARGADO, [línea vacía], N, DNI, RUTA 5 (nombre), días del período, HORAS (total)
     * @param registros Lista de registros de asistencia
     * @param empleados Lista de empleados para obtener nombres
     * @param periodoInicio Fecha de inicio del período (día 21)
     * @param periodoFin Fecha de fin del período (día 20)
     * @param nombreEncargado Nombre del encargado del sector
     * @param ausencias Lista de ausencias para marcar con 0 horas
     * @return Ruta del archivo generado
     */
    suspend fun exportarRegistrosAsistencia(
        registros: List<RegistroAsistencia>,
        empleados: List<Empleado>,
        periodoInicio: java.time.LocalDate,
        periodoFin: java.time.LocalDate,
        nombreEncargado: String = "",
        ausencias: List<Ausencia> = emptyList(),
        nombreSector: String = ""
    ): String {
        // Generar nombre del archivo: asistencia[NombreDelSector][FechaDelDiaDeExportacion].xls
        val fechaHoy = java.time.LocalDate.now()
        val fechaFormato = fechaHoy.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
        val sectorLimpio = nombreSector.replace(" ", "").uppercase() // Quitar espacios y poner en mayúsculas
        val nombreArchivo = if (sectorLimpio.isNotBlank()) {
            "asistencia${sectorLimpio}${fechaFormato}.xls"
        } else {
            "asistencia${fechaFormato}.xls" // Si no hay sector, solo fecha
        }
        val file = File(context.cacheDir, nombreArchivo)
        
        android.util.Log.d("ExcelExport", "=== INICIO EXPORTACIÓN EXCEL ===")
        android.util.Log.d("ExcelExport", "Total registros a exportar: ${registros.size}")
        android.util.Log.d("ExcelExport", "Total empleados disponibles: ${empleados.size}")
        android.util.Log.d("ExcelExport", "Período: $periodoInicio a $periodoFin")
        android.util.Log.d("ExcelExport", "Ruta archivo: ${file.absolutePath}")
        
        // Generar lista de días del período (21 al 31, luego 1 al 20)
        val diasDelPeriodo = generarDiasDelPeriodo(periodoInicio, periodoFin)
        android.util.Log.d("ExcelExport", "Días del período: ${diasDelPeriodo.joinToString(", ")}")
        
        var outputStream: FileOutputStream? = null
        
        try {
            // Verificar contexto y permisos
            android.util.Log.d("ExcelExport", "Cache dir: ${context.cacheDir.absolutePath}")
            android.util.Log.d("ExcelExport", "Cache dir existe: ${context.cacheDir.exists()}")
            android.util.Log.d("ExcelExport", "Cache dir es directorio: ${context.cacheDir.isDirectory}")
            android.util.Log.d("ExcelExport", "Cache dir escribible: ${context.cacheDir.canWrite()}")
            
            // Verificar que hay datos
            if (registros.isEmpty()) {
                throw IllegalStateException("No hay registros para exportar")
            }
            
            // Verificar permisos de escritura
            if (!context.cacheDir.canWrite()) {
                throw IllegalStateException("No se puede escribir en el directorio de cache")
            }
            
            // Crear directorio si no existe
            file.parentFile?.mkdirs()
            android.util.Log.d("ExcelExport", "Directorio creado/verificado")
            
            // Verificar que el archivo se puede crear
            if (file.exists()) {
                file.delete()
                android.util.Log.d("ExcelExport", "Archivo existente eliminado")
            }
            
            // Crear workbook con JExcelApi (compatible con Android)
            var writableWorkbook: WritableWorkbook? = null
            try {
                outputStream = FileOutputStream(file)
                writableWorkbook = Workbook.createWorkbook(outputStream)
                android.util.Log.d("ExcelExport", "WritableWorkbook creado exitosamente")
                
                val sheet = writableWorkbook.createSheet("Asistencias", 0)
                android.util.Log.d("ExcelExport", "Sheet creada")
                
                var rowNum = 0
                
                // FILA 0: ENCARGADO (título)
                sheet.addCell(Label(0, rowNum++, "ENCARGADO"))
                
                // FILA 1: Nombre del encargado
                sheet.addCell(Label(0, rowNum++, nombreEncargado))
                
                // FILA 2: Vacía
                rowNum++
                
                // FILA 3: ENCABEZADOS DE DATOS
                val headers = mutableListOf("N", "DNI", "RUTA 5")
                
                // Agregar columnas de días
                diasDelPeriodo.forEach { dia ->
                    headers.add(dia.toString())
                }
                headers.add("HORAS")
                headers.add("OBSERVACIONES")
                
                headers.forEachIndexed { index, header ->
                    sheet.addCell(Label(index, rowNum, header))
                }
                rowNum++
                android.util.Log.d("ExcelExport", "Encabezados creados: ${headers.size} columnas")
                
        // Crear mapa de empleados por clave consistente usada en la app (legajoKey)
        val empleadosMap = empleados.associateBy { emp ->
            emp.legajo ?: "SIN_LEGAJO_${emp.nombreCompleto.hashCode()}"
        }
                android.util.Log.d("ExcelExport", "Mapa de empleados creado: ${empleadosMap.size} empleados")
                
                // Agrupar registros por empleado (usar legajo del empleado)
                val registrosPorEmpleado = registros.groupBy { it.legajoEmpleado }
                android.util.Log.d("ExcelExport", "Registros agrupados por ${registrosPorEmpleado.size} empleados")
                
                // Obtener empleados que solo tienen ausencias (sin registros de asistencia)
                val empleadosConAusencias = ausencias.map { it.legajoEmpleado }.distinct()
                val empleadosSoloConAusencias = empleadosConAusencias.filter { legajo ->
                    legajo !in registrosPorEmpleado.keys
                }
                android.util.Log.d("ExcelExport", "Empleados solo con ausencias: ${empleadosSoloConAusencias.size}")
                
                // Crear lista combinada de todos los empleados a exportar
                val todosLosEmpleados = registrosPorEmpleado.keys.toMutableSet()
                todosLosEmpleados.addAll(empleadosSoloConAusencias)
                
                // ELIMINAR DUPLICADOS comparando DNI únicamente (si existe)
                val empleadosUnicos = mutableSetOf<String>()
                val legajosUnicos = mutableListOf<String>()
                
                todosLosEmpleados.forEach { legajo ->
                    val empleado = empleadosMap[legajo]
                    val dniKey = empleado?.legajo ?: legajo
                    
                    if (!empleadosUnicos.contains(dniKey)) {
                        empleadosUnicos.add(dniKey)
                        legajosUnicos.add(legajo)
                    }
                }
                android.util.Log.d("ExcelExport", "Total empleados a exportar (únicos): ${legajosUnicos.size}")
                
                var filaIndex = rowNum
                var totalHorasGeneral = 0
                
                legajosUnicos.forEach { legajoEmpleado ->
                    val registrosEmpleado = registrosPorEmpleado[legajoEmpleado] ?: emptyList()
                    try {
                        val empleado = empleadosMap[legajoEmpleado] ?: return@forEach
                        val nombreCompleto = empleado.nombreCompleto
                        
                        // N (número de fila)
                        sheet.addCell(Number(0, filaIndex, filaIndex.toDouble()))
                        
                        // DNI (mostrar "Sin datos" si corresponde)
                        val dniParaMostrar = when {
                            empleado?.legajo != null -> empleado.legajo
                            legajoEmpleado.startsWith("SIN_LEGAJO_") -> "Sin datos"
                            else -> legajoEmpleado
                        }
                        sheet.addCell(Label(1, filaIndex, dniParaMostrar))
                        
                        // RUTA 5 (nombre del empleado)
                        sheet.addCell(Label(2, filaIndex, nombreCompleto))
                        
                        // Crear mapa de horas por fecha para este empleado
                        val horasPorFecha = registrosEmpleado.associateBy { 
                            // Convertir fecha string a día del mes
                            val fecha = java.time.LocalDate.parse(it.fecha)
                            fecha.dayOfMonth
                        }
                        
                        // Crear mapa de fechas con ausencias para este empleado
                        val ausenciasEmpleado = ausencias.filter { 
                            it.legajoEmpleado == legajoEmpleado 
                        }
                        val fechasConAusencia = mutableSetOf<Int>()
                        ausenciasEmpleado.forEach { ausencia ->
                            val fechasAfectadas = ausencia.getFechasAfectadas()
                            fechasAfectadas.forEach { fecha ->
                                if (fecha.dayOfMonth in diasDelPeriodo) {
                                    fechasConAusencia.add(fecha.dayOfMonth)
                                }
                            }
                        }
                        
                        var totalHoras = 0
                        
                        // Llenar columnas de días
                        diasDelPeriodo.forEachIndexed { colIndex, dia ->
                            // Si hay ausencia en este día, escribir 0
                            if (dia in fechasConAusencia) {
                                sheet.addCell(Number(3 + colIndex, filaIndex, 0.0))
                                // No sumar al total si es ausencia
                            } else {
                                val horasDelDia = horasPorFecha[dia]?.horasTrabajadas ?: 0
                                if (horasDelDia > 0) {
                                    sheet.addCell(Number(3 + colIndex, filaIndex, horasDelDia.toDouble()))
                                    totalHoras += horasDelDia
                                }
                                // Si horasDelDia es 0, la celda queda vacía
                            }
                        }
                        
                        // Columna HORAS (total)
                        sheet.addCell(Number(3 + diasDelPeriodo.size, filaIndex, totalHoras.toDouble()))
                        totalHorasGeneral += totalHoras
                        
                        // Columna OBSERVACIONES: priorizar observaciones de los registros del período, con fecha dd/MM
                        val obsDeRegistros = registrosEmpleado
                            .filter { !it.observaciones.isNullOrBlank() }
                            .map { reg ->
                                val fecha = try {
                                    val f = java.time.LocalDate.parse(reg.fecha)
                                    String.format("%02d/%02d", f.dayOfMonth, f.monthValue)
                                } catch (e: Exception) { "--/--" }
                                "$fecha: ${reg.observaciones!!.trim()}"
                            }
                            .filter { it.isNotEmpty() }
                            .distinct()
                        val observacionesCombinadas = if (obsDeRegistros.isNotEmpty()) obsDeRegistros.joinToString(" | ") else empleado?.observacion?.trim().orEmpty()
                        if (observacionesCombinadas.isNotBlank()) {
                            sheet.addCell(Label(4 + diasDelPeriodo.size, filaIndex, observacionesCombinadas))
                        }
                        
                        android.util.Log.d("ExcelExport", "Empleado $filaIndex: $nombreCompleto - $totalHoras horas totales - Obs: $observacionesCombinadas")
                        filaIndex++
                        
                    } catch (e: Exception) {
                        android.util.Log.e("ExcelExport", "Error procesando empleado $legajoEmpleado", e)
                        throw e
                    }
                }
                
                // Fila del encargado al final (como empleado, 8h por día automático)
                val nombreEncargadoMostrar = nombreEncargado.ifBlank { "Encargado" }
                val horasPorDiaEncargado = 8
                val totalHorasEncargado = horasPorDiaEncargado * diasDelPeriodo.size
                sheet.addCell(Number(0, filaIndex, filaIndex.toDouble()))
                sheet.addCell(Label(1, filaIndex, ""))
                sheet.addCell(Label(2, filaIndex, nombreEncargadoMostrar))
                diasDelPeriodo.forEachIndexed { colIndex, _ ->
                    sheet.addCell(Number(3 + colIndex, filaIndex, horasPorDiaEncargado.toDouble()))
                }
                sheet.addCell(Number(3 + diasDelPeriodo.size, filaIndex, totalHorasEncargado.toDouble()))
                sheet.addCell(Label(4 + diasDelPeriodo.size, filaIndex, ""))
                totalHorasGeneral += totalHorasEncargado
                filaIndex++
                android.util.Log.d("ExcelExport", "Fila encargado agregada: $nombreEncargadoMostrar - $totalHorasEncargado horas (8h/día)")
                
                // Fila de totales al final
                sheet.addCell(Label(0, filaIndex, "")) // N vacío
                sheet.addCell(Label(1, filaIndex, "")) // DNI vacío
                sheet.addCell(Label(2, filaIndex, "TOTAL")) // RUTA 5: "TOTAL"
                
                // Dejar días vacíos
                diasDelPeriodo.forEachIndexed { colIndex, _ ->
                    sheet.addCell(Label(3 + colIndex, filaIndex, ""))
                }
                
                // Columna HORAS: total general
                sheet.addCell(Number(3 + diasDelPeriodo.size, filaIndex, totalHorasGeneral.toDouble()))
                
                // Columna OBSERVACIONES vacía
                sheet.addCell(Label(4 + diasDelPeriodo.size, filaIndex, ""))
                
                android.util.Log.d("ExcelExport", "Fila de totales agregada: $totalHorasGeneral horas")
                android.util.Log.d("ExcelExport", "Todos los empleados procesados: ${registrosPorEmpleado.size}")
                
                // Escribir y cerrar workbook
                writableWorkbook.write()
                android.util.Log.d("ExcelExport", "Archivo escrito exitosamente")
                
            } catch (e: Exception) {
                android.util.Log.e("ExcelExport", "Error creando/escribiendo Excel", e)
                throw IllegalStateException("Error al crear el archivo Excel: ${e.message}")
            } finally {
                // Cerrar streams de forma segura
                try {
                    writableWorkbook?.close()
                    android.util.Log.d("ExcelExport", "WritableWorkbook cerrado")
                } catch (e: Exception) {
                    android.util.Log.e("ExcelExport", "Error cerrando WritableWorkbook", e)
                }
                
                try {
                    outputStream?.close()
                    android.util.Log.d("ExcelExport", "FileOutputStream cerrado")
                } catch (e: Exception) {
                    android.util.Log.e("ExcelExport", "Error cerrando FileOutputStream", e)
                }
            }
            
            android.util.Log.d("ExcelExport", "=== EXPORTACIÓN EXCEL COMPLETADA ===")
            android.util.Log.d("ExcelExport", "Archivo guardado en: ${file.absolutePath}")
            android.util.Log.d("ExcelExport", "Tamaño del archivo: ${file.length()} bytes")
            
            return file.absolutePath
            
        } catch (e: Exception) {
            android.util.Log.e("ExcelExport", "=== ERROR CRÍTICO EN EXPORTACIÓN ===", e)
            android.util.Log.e("ExcelExport", "Tipo de error: ${e.javaClass.simpleName}")
            android.util.Log.e("ExcelExport", "Mensaje: ${e.message}")
            android.util.Log.e("ExcelExport", "Stack trace completo:", e)
            
            throw e
        } finally {
            try {
                outputStream?.close()
                android.util.Log.d("ExcelExport", "Recursos liberados")
            } catch (e: Exception) {
                android.util.Log.e("ExcelExport", "Error al cerrar recursos", e)
            }
        }
    }
    
    /**
     * Genera la lista de días del período (21 al 31, luego 1 al 20).
     */
    private fun generarDiasDelPeriodo(inicio: java.time.LocalDate, fin: java.time.LocalDate): List<Int> {
        val dias = mutableListOf<Int>()
        
        // Días del mes anterior (21 al 31)
        for (dia in 21..31) {
            dias.add(dia)
        }
        
        // Días del mes actual (1 al 20)
        for (dia in 1..20) {
            dias.add(dia)
        }
        
        return dias
    }
    
    /**
     * Genera un nombre de archivo único.
     */
    private fun generarNombreArchivo(
        tipo: String,
        inicio: java.time.LocalDate,
        fin: java.time.LocalDate,
        extension: String
    ): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return "reporte_${tipo}_${inicio.format(formatter)}_${fin.format(formatter)}.$extension"
    }
    
}
