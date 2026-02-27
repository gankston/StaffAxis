package com.registro.empleados.data.export

import android.content.Context
import android.util.Log
import com.registro.empleados.domain.model.Ausencia
import com.registro.empleados.domain.model.Empleado
import com.registro.empleados.domain.model.RegistroAsistencia
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio para exportar datos a formato CSV con formato de tabla de asistencia.
 * Formato: N, DNI, RUTA 5 (nombre), días del período (21-31, 1-20), HORAS (total)
 */
@Singleton
class CsvExportService @Inject constructor(
    private val context: Context
) {
    
    /**
     * Exporta registros de asistencia a un archivo CSV con formato de tabla de asistencia.
     * Formato: ENCARGADO, [línea vacía], N, DNI, RUTA 5 (nombre), días del período, HORAS (total)
     * @param registros Lista de registros de asistencia
     * @param empleados Lista de empleados para obtener nombres
     * @param periodoInicio Fecha de inicio del período (día 21)
     * @param periodoFin Fecha de fin del período (día 20)
     * @param nombreEncargado Nombre del encargado del sector
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
        // Generar nombre del archivo: asistencia[NombreDelSector][FechaDelDiaDeExportacion].csv
        val fechaHoy = java.time.LocalDate.now()
        val fechaFormato = fechaHoy.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
        val sectorLimpio = nombreSector.replace(" ", "").uppercase() // Quitar espacios y poner en mayúsculas
        val fileName = if (sectorLimpio.isNotBlank()) {
            "asistencia${sectorLimpio}${fechaFormato}.csv"
        } else {
            "asistencia${fechaFormato}.csv" // Si no hay sector, solo fecha
        }
        
        Log.d("CsvExport", "=== INICIANDO EXPORTACIÓN CSV ===")
        Log.d("CsvExport", "Registros a exportar: ${registros.size}")
        Log.d("CsvExport", "Empleados disponibles: ${empleados.size}")
        Log.d("CsvExport", "Período: $periodoInicio a $periodoFin")
        
        // Usar cacheDir para evitar permisos de almacenamiento externo
        val directory = context.cacheDir
        val file = File(directory, fileName)
        
        Log.d("CsvExport", "Directorio: ${directory.absolutePath}")
        Log.d("CsvExport", "Archivo: ${file.absolutePath}")
        
        // Generar lista de días del período (21 al 31, luego 1 al 20)
        val diasDelPeriodo = generarDiasDelPeriodo(periodoInicio, periodoFin)
        Log.d("CsvExport", "Días del período: ${diasDelPeriodo.joinToString(", ")}")
        
        var fileWriter: FileWriter? = null
        
        try {
            // Verificar que hay datos
            if (registros.isEmpty()) {
                throw IllegalStateException("No hay registros para exportar")
            }
            
            // Crear directorio si no existe
            file.parentFile?.mkdirs()
            Log.d("CsvExport", "Directorio creado/verificado")
            
            // Crear archivo
            file.createNewFile()
            Log.d("CsvExport", "Archivo CSV creado")
            
            fileWriter = FileWriter(file)
            Log.d("CsvExport", "FileWriter inicializado")
            
        // Crear mapa de empleados por clave consistente usada en toda la app
        // Si el DNI es nulo, usamos una clave sintética estable basada en el nombre
        val empleadosMap = empleados.associateBy { emp ->
            emp.legajo ?: "SIN_LEGAJO_${emp.nombreCompleto.hashCode()}"
        }
            Log.d("CsvExport", "Mapa de empleados creado: ${empleadosMap.size} empleados")
            
            // AGREGAR ENCABEZADO DE ENCARGADO
            fileWriter.append("ENCARGADO\n")
            fileWriter.append("\"${nombreEncargado}\"\n")
            fileWriter.append("\n")  // Línea en blanco
            
            // ENCABEZADOS DE DATOS
            val headers = mutableListOf("N", "DNI", "RUTA 5")
            
            // Agregar columnas de días
            diasDelPeriodo.forEach { dia ->
                headers.add(dia.toString())
            }
            headers.add("HORAS")
            headers.add("OBSERVACIONES")
            
            // Escribir encabezados
            fileWriter.append(headers.joinToString(","))
            fileWriter.append("\n")
            Log.d("CsvExport", "Encabezados escritos: ${headers.size} columnas")
            
            // Agrupar registros por empleado (usar legajo del empleado)
            val registrosPorEmpleado = registros.groupBy { it.legajoEmpleado }
            Log.d("CsvExport", "Registros agrupados por ${registrosPorEmpleado.size} empleados")
            
            // Obtener empleados que solo tienen ausencias (sin registros de asistencia)
            val empleadosConAusencias = ausencias.map { it.legajoEmpleado }.distinct()
            val empleadosSoloConAusencias = empleadosConAusencias.filter { legajo ->
                legajo !in registrosPorEmpleado.keys
            }
            Log.d("CsvExport", "Empleados solo con ausencias: ${empleadosSoloConAusencias.size}")
            
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
            Log.d("CsvExport", "Total empleados a exportar (únicos): ${legajosUnicos.size}")
            
            var filaIndex = 1
            legajosUnicos.forEach { legajoEmpleado ->
                val registrosEmpleado = registrosPorEmpleado[legajoEmpleado] ?: emptyList()
                try {
                    val empleado = empleadosMap[legajoEmpleado] ?: return@forEach
                    val nombreCompleto = empleado.nombreCompleto
                    
                    // Crear fila
                    val fila = mutableListOf<String>()
                    
                    // N (número de fila)
                    fila.add(filaIndex.toString())
                    
                    // DNI (mostrar "Sin datos" si no tiene)
                    val dniParaMostrar = when {
                        empleado?.legajo != null -> empleado.legajo
                        legajoEmpleado.startsWith("SIN_LEGAJO_") -> "Sin datos"
                        else -> legajoEmpleado
                    }
                    fila.add(dniParaMostrar)
                    
                    // RUTA 5 (nombre del empleado)
                    fila.add("\"$nombreCompleto\"") // Entre comillas por si tiene espacios
                    
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
                    diasDelPeriodo.forEach { dia ->
                        // Si hay ausencia en este día, escribir 0
                        if (dia in fechasConAusencia) {
                            fila.add("0")
                            // No sumar al total si es ausencia
                        } else {
                            val horasDelDia = horasPorFecha[dia]?.horasTrabajadas ?: 0
                            if (horasDelDia > 0) {
                                fila.add(horasDelDia.toString())
                                totalHoras += horasDelDia
                            } else {
                                fila.add("") // Celda vacía si no hay horas
                            }
                        }
                    }
                    
                    // Columna HORAS (total)
                    fila.add(totalHoras.toString())
                    
                    // Columna OBSERVACIONES: priorizar observaciones de registros del período, con fecha dd/MM
                    val obsDeRegistros = registrosEmpleado
                        .filter { !it.observaciones.isNullOrBlank() }
                        .map { reg ->
                            val fecha = try {
                                val f = java.time.LocalDate.parse(reg.fecha)
                                String.format("%02d/%02d", f.dayOfMonth, f.monthValue)
                            } catch (e: Exception) { "--/--" }
                            "$fecha: ${(reg.observaciones ?: "").trim()}"
                        }
                        .filter { it.isNotEmpty() }
                        .distinct()
                    val observacionesCombinadas = if (obsDeRegistros.isNotEmpty()) {
                        obsDeRegistros.joinToString(" | ")
                    } else {
                        empleado?.observacion?.trim().orEmpty()
                    }
                    fila.add("\"${observacionesCombinadas}\"")
                    
                    // Escribir fila
                    fileWriter.append(fila.joinToString(","))
                    fileWriter.append("\n")
                    
                    Log.d("CsvExport", "Empleado $filaIndex: $nombreCompleto - $totalHoras horas totales - Obs: $observacionesCombinadas")
                    filaIndex++
                    
                } catch (e: Exception) {
                    Log.e("CsvExport", "Error procesando empleado $legajoEmpleado", e)
                    throw e
                }
            }
            
            // Fila del encargado al final (como empleado, 8h por día automático)
            val nombreEncargadoMostrar = nombreEncargado.ifBlank { "Encargado" }
            val horasPorDiaEncargado = 8
            val totalHorasEncargado = horasPorDiaEncargado * diasDelPeriodo.size
            val filaEncargado = mutableListOf<String>()
            filaEncargado.add(filaIndex.toString())
            filaEncargado.add("")
            filaEncargado.add("\"$nombreEncargadoMostrar\"")
            diasDelPeriodo.forEach { _ ->
                filaEncargado.add(horasPorDiaEncargado.toString())
            }
            filaEncargado.add(totalHorasEncargado.toString())
            filaEncargado.add("")
            fileWriter.append(filaEncargado.joinToString(","))
            fileWriter.append("\n")
            Log.d("CsvExport", "Fila encargado agregada: $nombreEncargadoMostrar - $totalHorasEncargado horas (8h/día)")
            
            Log.d("CsvExport", "Todos los empleados procesados: ${registrosPorEmpleado.size}")
            
            // Cerrar archivo
            fileWriter.flush()
            fileWriter.close()
            
            Log.d("CsvExport", "=== EXPORTACIÓN CSV COMPLETADA ===")
            Log.d("CsvExport", "Archivo guardado en: ${file.absolutePath}")
            Log.d("CsvExport", "Tamaño del archivo: ${file.length()} bytes")
            
            return file.absolutePath
            
        } catch (e: Exception) {
            Log.e("CsvExport", "=== ERROR CRÍTICO EN EXPORTACIÓN CSV ===", e)
            Log.e("CsvExport", "Tipo de error: ${e.javaClass.simpleName}")
            Log.e("CsvExport", "Mensaje: ${e.message}")
            Log.e("CsvExport", "Stack trace completo:", e)
            
            throw e
        } finally {
            try {
                fileWriter?.close()
                Log.d("CsvExport", "Recursos liberados")
            } catch (e: Exception) {
                Log.e("CsvExport", "Error al cerrar recursos", e)
            }
        }
    }
    
    /**
     * Genera la lista de días del período (26 al 31, luego 1 al 25).
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
