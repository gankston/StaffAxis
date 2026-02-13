package com.registro.empleados.domain.usecase.ausencia

import com.registro.empleados.domain.model.Ausencia
import com.registro.empleados.domain.repository.AusenciaRepository
import javax.inject.Inject

class EliminarAusenciaUseCase @Inject constructor(
    private val ausenciaRepository: AusenciaRepository
) {
    suspend operator fun invoke(ausencia: Ausencia) {
        android.util.Log.d("EliminarAusenciaUseCase", "Eliminando ausencia ID: ${ausencia.id}")
        ausenciaRepository.deleteAusencia(ausencia)
        android.util.Log.d("EliminarAusenciaUseCase", "Ausencia eliminada exitosamente")
    }
}

