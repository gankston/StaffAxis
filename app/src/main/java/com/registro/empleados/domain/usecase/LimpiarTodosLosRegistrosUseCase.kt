package com.registro.empleados.domain.usecase

import android.util.Log
import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import javax.inject.Inject

/**
 * UseCase para limpiar todos los registros de asistencia.
 * Esto resetea las horas de todos los empleados a 0.
 */
class LimpiarTodosLosRegistrosUseCase @Inject constructor(
    private val registroRepository: RegistroAsistenciaRepository
) {
    suspend operator fun invoke() {
        try {
            Log.d("LimpiarRegistros", "🧹 === LIMPIANDO TODOS LOS REGISTROS DE ASISTENCIA ===")
            
            registroRepository.deleteAllRegistros()
            
            Log.d("LimpiarRegistros", "✅ Todos los registros han sido eliminados")
            Log.d("LimpiarRegistros", "✅ Todas las tarjetas ahora mostrarán estado neutral (sin horas)")
            
        } catch (e: Exception) {
            Log.e("LimpiarRegistros", "❌ Error limpiando registros", e)
        }
    }
}


