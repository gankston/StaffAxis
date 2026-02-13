package com.registro.empleados.domain.usecase.ausencia

import com.registro.empleados.domain.model.Ausencia
import com.registro.empleados.domain.repository.AusenciaRepository
import java.time.LocalDate
import javax.inject.Inject

class GetAusenciasByFechaUseCase @Inject constructor(
    private val ausenciaRepository: AusenciaRepository
) {
    suspend operator fun invoke(fecha: LocalDate): List<Ausencia> {
        return ausenciaRepository.getAusenciasByFecha(fecha)
    }
}
