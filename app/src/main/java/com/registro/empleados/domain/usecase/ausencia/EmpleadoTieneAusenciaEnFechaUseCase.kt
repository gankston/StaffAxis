package com.registro.empleados.domain.usecase.ausencia

import com.registro.empleados.domain.repository.AusenciaRepository
import java.time.LocalDate
import javax.inject.Inject

class EmpleadoTieneAusenciaEnFechaUseCase @Inject constructor(
    private val ausenciaRepository: AusenciaRepository
) {
    suspend operator fun invoke(legajo: String, fecha: LocalDate): Boolean {
        val resultado = ausenciaRepository.empleadoTieneAusenciaEnFecha(legajo, fecha)
        android.util.Log.d("AusenciaUseCase", "Verificando ausencia - Legajo: $legajo, Fecha: $fecha, Resultado: $resultado")
        return resultado
    }
}
