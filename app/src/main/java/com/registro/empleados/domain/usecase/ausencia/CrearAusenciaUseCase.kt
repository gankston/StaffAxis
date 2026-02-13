package com.registro.empleados.domain.usecase.ausencia

import com.registro.empleados.domain.model.Ausencia
import com.registro.empleados.domain.repository.AusenciaRepository
import javax.inject.Inject

class CrearAusenciaUseCase @Inject constructor(
    private val ausenciaRepository: AusenciaRepository
) {
    suspend operator fun invoke(ausencia: Ausencia): Long {
        android.util.Log.d("CrearAusenciaUseCase", "Creando ausencia: ${ausencia.legajoEmpleado} del ${ausencia.fechaInicio} al ${ausencia.fechaFin}")
        val resultado = ausenciaRepository.insertAusencia(ausencia)
        android.util.Log.d("CrearAusenciaUseCase", "Ausencia creada con ID: $resultado")
        return resultado
    }
}
