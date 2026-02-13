package com.registro.empleados.domain.usecase.database

import android.util.Log
import com.registro.empleados.domain.repository.EmpleadoRepository
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * UseCase para limpiar completamente la base de datos.
 * Elimina todos los empleados y registros de asistencia.
 */
class LimpiarBaseDatosUseCase @Inject constructor(
    private val empleadoRepository: EmpleadoRepository,
    private val registroAsistenciaRepository: RegistroAsistenciaRepository
) {

    /**
     * Resultado de la operación de limpieza.
     */
    data class ResultadoLimpieza(
        val exito: Boolean,
        val empleadosEliminados: Int,
        val registrosEliminados: Int,
        val mensaje: String
    )

    /**
     * Limpia completamente la base de datos.
     * @return Resultado de la operación
     */
    suspend operator fun invoke(): ResultadoLimpieza {
        return try {
            Log.d("LimpiarBaseDatosUseCase", "=== INICIANDO LIMPIEZA COMPLETA DE BASE DE DATOS ===")
            
            // Contar registros antes de eliminar para el reporte
            val empleadosAntes = empleadoRepository.getAllEmpleadosActivos().first().size
            val registrosAntes = registroAsistenciaRepository.getAllRegistros().first().size
            
            Log.d("LimpiarBaseDatosUseCase", "Empleados a eliminar: $empleadosAntes")
            Log.d("LimpiarBaseDatosUseCase", "Registros a eliminar: $registrosAntes")
            
            // Eliminar todos los registros de asistencia primero
            Log.d("LimpiarBaseDatosUseCase", "Eliminando registros de asistencia...")
            registroAsistenciaRepository.deleteAllRegistros()
            
            // Eliminar todos los empleados
            Log.d("LimpiarBaseDatosUseCase", "Eliminando empleados...")
            empleadoRepository.deleteAllEmpleados()
            
            Log.d("LimpiarBaseDatosUseCase", "=== LIMPIEZA COMPLETADA EXITOSAMENTE ===")
            
            ResultadoLimpieza(
                exito = true,
                empleadosEliminados = empleadosAntes,
                registrosEliminados = registrosAntes,
                mensaje = "Base de datos limpiada completamente. Se eliminaron $empleadosAntes empleados y $registrosAntes registros de asistencia."
            )
            
        } catch (e: Exception) {
            Log.e("LimpiarBaseDatosUseCase", "ERROR al limpiar base de datos", e)
            ResultadoLimpieza(
                exito = false,
                empleadosEliminados = 0,
                registrosEliminados = 0,
                mensaje = "Error al limpiar la base de datos: ${e.message ?: "Error desconocido"}"
            )
        }
    }
}
