package com.registro.empleados.domain.usecase.empleado

import com.registro.empleados.domain.repository.RegistroAsistenciaRepository
import java.time.LocalDate
import javax.inject.Inject

class TieneHorasCargadasHoyUseCase @Inject constructor(
    private val registroRepository: RegistroAsistenciaRepository
) {
    suspend operator fun invoke(legajo: String): Boolean {
        return try {
            val hoy = LocalDate.now()
            val registrosHoy = registroRepository.getRegistrosByLegajoAndFecha(legajo, hoy)
            val tieneRegistrosHoy = registrosHoy.isNotEmpty()
            
            android.util.Log.d("TieneHorasCargadasHoyUseCase", "Legajo: $legajo, Tiene registros hoy: $tieneRegistrosHoy, Resultado: $tieneRegistrosHoy")
            
            return tieneRegistrosHoy
        } catch (e: Exception) {
            android.util.Log.e("TieneHorasCargadasHoyUseCase", "Error para legajo $legajo", e)
            false
        }
    }
}
