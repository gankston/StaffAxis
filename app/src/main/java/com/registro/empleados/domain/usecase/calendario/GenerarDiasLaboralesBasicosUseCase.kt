package com.registro.empleados.domain.usecase.calendario

import com.registro.empleados.domain.model.DiaLaboral
import com.registro.empleados.domain.repository.DiaLaboralRepository
import javax.inject.Inject

/**
 * Caso de uso para generar días laborales básicos.
 * Crea días laborales con fines de semana marcados como no laborales.
 * Se usa cuando no hay datos de feriados disponibles.
 */
class GenerarDiasLaboralesBasicosUseCase @Inject constructor(
    private val diaLaboralRepository: DiaLaboralRepository
) {
    
    /**
     * Ejecuta el caso de uso para generar días laborales básicos para un año.
     * @param año Año para generar los días laborales
     */
    suspend operator fun invoke(año: Int, mes: Int) {
        android.util.Log.d("GenerarDias", "Generando días para $mes/$año")
        
        val primerDia = java.time.LocalDate.of(año, mes, 1)
        val ultimoDia = primerDia.plusMonths(1).minusDays(1)
        
        // Limpiar días existentes para este mes
        try {
            android.util.Log.d("GenerarDias", "Limpiando días existentes para el mes...")
            diaLaboralRepository.eliminarDiasAntiguos(primerDia.minusDays(1))
        } catch (e: Exception) {
            android.util.Log.w("GenerarDias", "Error limpiando días existentes", e)
        }
        
        val dias = mutableListOf<DiaLaboral>()
        
        var fecha = primerDia
        while (!fecha.isAfter(ultimoDia)) {
            val esFinde = fecha.dayOfWeek == java.time.DayOfWeek.SATURDAY || fecha.dayOfWeek == java.time.DayOfWeek.SUNDAY
            
            android.util.Log.d("GenerarDias", "Fecha: $fecha, Día: ${fecha.dayOfWeek}, EsFinde: $esFinde")
            
            val dia = DiaLaboral(
                fecha = fecha,
                esLaboral = !esFinde,
                tipoDia = if (esFinde) DiaLaboral.TipoDia.FIN_DE_SEMANA else DiaLaboral.TipoDia.LABORAL,
                descripcion = if (esFinde) "Fin de semana" else null
            )
            
            dias.add(dia)
            fecha = fecha.plusDays(1)
        }
        
        android.util.Log.d("GenerarDias", "Insertando ${dias.size} días en BD...")
        diaLaboralRepository.insertDias(dias)
        android.util.Log.d("GenerarDias", "Días insertados exitosamente")
    }
    
    /**
     * Genera días laborales básicos para un año específico.
     * Los fines de semana se marcan como no laborales.
     */
    private fun generateDiasLaboralesForYear(año: Int): List<DiaLaboral> {
        val dias = mutableListOf<DiaLaboral>()
        val inicioAño = java.time.LocalDate.of(año, 1, 1)
        val finAño = java.time.LocalDate.of(año, 12, 31)
        
        var fechaActual = inicioAño
        while (!fechaActual.isAfter(finAño)) {
            val esFinDeSemana = fechaActual.dayOfWeek.value >= 6 // 6 = Sábado, 7 = Domingo
            val tipoDia = if (esFinDeSemana) {
                DiaLaboral.TipoDia.FIN_DE_SEMANA
            } else {
                DiaLaboral.TipoDia.LABORAL
            }
            
            val dia = DiaLaboral(
                fecha = fechaActual,
                esLaboral = !esFinDeSemana,
                tipoDia = tipoDia,
                descripcion = if (esFinDeSemana) {
                    when (fechaActual.dayOfWeek) {
                        java.time.DayOfWeek.SATURDAY -> "Sábado"
                        java.time.DayOfWeek.SUNDAY -> "Domingo"
                        else -> "Fin de semana"
                    }
                } else null
            )
            
            dias.add(dia)
            fechaActual = fechaActual.plusDays(1)
        }
        
        return dias
    }
    
    /**
     * Genera días laborales para un período específico (26 al 25).
     * Útil para inicializar datos de un período específico.
     */
    suspend fun generarParaPeriodo(
        fechaInicio: java.time.LocalDate,
        fechaFin: java.time.LocalDate
    ) {
        val diasLaborales = generateDiasLaboralesForPeriod(fechaInicio, fechaFin)
        diaLaboralRepository.insertDias(diasLaborales)
    }
    
    /**
     * Genera días laborales para un período específico.
     */
    private fun generateDiasLaboralesForPeriod(
        fechaInicio: java.time.LocalDate,
        fechaFin: java.time.LocalDate
    ): List<DiaLaboral> {
        val dias = mutableListOf<DiaLaboral>()
        var fechaActual = fechaInicio
        
        while (!fechaActual.isAfter(fechaFin)) {
            val esFinDeSemana = fechaActual.dayOfWeek.value >= 6
            val tipoDia = if (esFinDeSemana) {
                DiaLaboral.TipoDia.FIN_DE_SEMANA
            } else {
                DiaLaboral.TipoDia.LABORAL
            }
            
            val dia = DiaLaboral(
                fecha = fechaActual,
                esLaboral = !esFinDeSemana,
                tipoDia = tipoDia,
                descripcion = if (esFinDeSemana) {
                    when (fechaActual.dayOfWeek) {
                        java.time.DayOfWeek.SATURDAY -> "Sábado"
                        java.time.DayOfWeek.SUNDAY -> "Domingo"
                        else -> "Fin de semana"
                    }
                } else null
            )
            
            dias.add(dia)
            fechaActual = fechaActual.plusDays(1)
        }
        
        return dias
    }
}
