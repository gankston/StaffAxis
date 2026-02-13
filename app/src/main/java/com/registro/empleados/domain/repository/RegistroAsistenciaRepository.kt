package com.registro.empleados.domain.repository

import com.registro.empleados.domain.model.RegistroAsistencia
import java.time.LocalDate

/**
 * Interface del repositorio para operaciones con registros de asistencia.
 * Actualizada para nueva estructura: solo horas trabajadas manuales (4, 8 o 12 horas).
 */
interface RegistroAsistenciaRepository {
    
    /**
     * Obtiene registros de asistencia de un empleado en un rango de fechas.
     * @param legajo Legajo del empleado
     * @param fechaInicio Fecha de inicio del rango (formato "yyyy-MM-dd")
     * @param fechaFin Fecha de fin del rango (formato "yyyy-MM-dd")
     * @return Lista de registros ordenados por fecha descendente
     */
    suspend fun getRegistrosByLegajoYRango(
        legajo: String, 
        fechaInicio: String, 
        fechaFin: String
    ): List<RegistroAsistencia>

    /**
     * Obtiene todos los registros de asistencia en un rango de fechas.
     * @param fechaInicio Fecha de inicio del rango (formato "yyyy-MM-dd")
     * @param fechaFin Fecha de fin del rango (formato "yyyy-MM-dd")
     * @return Lista de registros ordenados por fecha descendente
     */
    suspend fun getRegistrosByRango(
        fechaInicio: String, 
        fechaFin: String
    ): List<RegistroAsistencia>

    /**
     * Obtiene el registro de asistencia de un empleado para una fecha específica.
     * @param legajo Legajo del empleado
     * @param fecha Fecha a consultar (formato "yyyy-MM-dd")
     * @return Registro encontrado o null si no existe
     */
    suspend fun getRegistroByLegajoYFecha(legajo: String, fecha: String): RegistroAsistencia?

    /**
     * Inserta un nuevo registro de asistencia.
     * @param registro Registro a insertar
     * @return ID del registro insertado
     */
    suspend fun insertRegistro(registro: RegistroAsistencia): Long

    /**
     * Actualiza un registro de asistencia existente.
     * @param registro Registro con los datos actualizados
     */
    suspend fun updateRegistro(registro: RegistroAsistencia)

    /**
     * Elimina un registro de asistencia.
     * @param id ID del registro a eliminar
     */
    suspend fun deleteRegistro(id: Long)

    /**
     * Obtiene el total de horas trabajadas por un empleado en un período.
     * @param legajo Legajo del empleado
     * @param fechaInicio Fecha de inicio del rango (formato "yyyy-MM-dd")
     * @param fechaFin Fecha de fin del rango (formato "yyyy-MM-dd")
     * @return Total de horas trabajadas o null si no hay registros
     */
    suspend fun getTotalHorasByLegajoYPeriodo(legajo: String, fechaInicio: String, fechaFin: String): Int?

    /**
     * Obtiene todos los registros de asistencia.
     * @return Lista de todos los registros
     */
    suspend fun getAllRegistros(): kotlinx.coroutines.flow.Flow<List<RegistroAsistencia>>

    /**
     * Elimina todos los registros de asistencia de la base de datos.
     * ¡CUIDADO! Esta operación es irreversible.
     */
    suspend fun deleteAllRegistros()

    /**
     * Obtiene registros de asistencia de un empleado para una fecha específica.
     * @param legajo Legajo del empleado
     * @param fecha Fecha a consultar (LocalDate)
     * @return Lista de registros encontrados
     */
    suspend fun getRegistrosByLegajoAndFecha(
        legajo: String,
        fecha: LocalDate
    ): List<RegistroAsistencia>
}
