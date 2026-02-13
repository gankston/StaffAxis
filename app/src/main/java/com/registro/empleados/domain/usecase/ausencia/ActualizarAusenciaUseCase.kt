package com.registro.empleados.domain.usecase.ausencia

import com.registro.empleados.domain.model.Ausencia
import com.registro.empleados.domain.repository.AusenciaRepository
import javax.inject.Inject

class ActualizarAusenciaUseCase @Inject constructor(
    private val ausenciaRepository: AusenciaRepository
) {
    suspend operator fun invoke(ausencia: Ausencia) {
        android.util.Log.d("ActualizarAusenciaUseCase", "Actualizando ausencia ID: ${ausencia.id}")
        ausenciaRepository.updateAusencia(ausencia)
        android.util.Log.d("ActualizarAusenciaUseCase", "Ausencia actualizada exitosamente")
    }
}

