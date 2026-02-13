package com.registro.empleados.domain.usecase.calendario

import com.registro.empleados.domain.model.PeriodoLaboral
import javax.inject.Inject

/**
 * Caso de uso para obtener el período laboral actual.
 * Encapsula la lógica de negocio para calcular períodos 26-25.
 */
class GetPeriodoActualUseCase @Inject constructor() {
    
    /**
     * Ejecuta el caso de uso para obtener el período laboral actual.
     * @return Período laboral actual (del 26 al 25)
     */
    operator fun invoke(): PeriodoLaboral {
        return PeriodoLaboral.calcularPeriodoActual()
    }
}
