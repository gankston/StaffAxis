package com.registro.empleados.data.repository

import com.registro.empleados.data.local.database.AppDatabase
import com.registro.empleados.data.database.daos.DiaLaboralDao
import com.registro.empleados.data.mapper.DiaLaboralMapper
import com.registro.empleados.domain.model.DiaLaboral
import com.registro.empleados.domain.repository.DiaLaboralRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de días laborales.
 */
@Singleton
class DiaLaboralRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : DiaLaboralRepository {
    
    private val diaLaboralDao: DiaLaboralDao = database.diaLaboralDao()

    override suspend fun getDiasByRango(
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): List<DiaLaboral> {
        val entities = diaLaboralDao.getDiasByRango(
            fechaInicio.toString(),
            fechaFin.toString()
        )
        return DiaLaboralMapper.toDomainList(entities)
    }

    override suspend fun getDiaByFecha(fecha: LocalDate): DiaLaboral? {
        val entity = diaLaboralDao.getDiaByFecha(fecha.toString())
        return entity?.let { DiaLaboralMapper.toDomain(it) }
    }

    override suspend fun esDiaLaboral(fecha: LocalDate): Boolean? {
        val dia = getDiaByFecha(fecha)
        return dia?.esLaboral
    }

    override suspend fun insertDias(dias: List<DiaLaboral>) {
        val entities = dias.map { DiaLaboralMapper.toEntity(it) }
        diaLaboralDao.insertDias(entities)
    }

    override suspend fun contarDiasLaborales(fechaInicio: LocalDate, fechaFin: LocalDate): Int {
        return diaLaboralDao.contarDiasLaborales(
            fechaInicio.toString(),
            fechaFin.toString()
        )
    }

    override suspend fun eliminarDiasAntiguos(fechaLimite: LocalDate) {
        diaLaboralDao.eliminarDiasAntiguos(fechaLimite.toString())
    }
    
    suspend fun limpiarTodosLosDias() {
        diaLaboralDao.eliminarDiasAntiguos("1900-01-01")
    }

    override suspend fun sincronizarFeriados(año: Int) {
        // TODO: Implementar sincronización con API de feriados
        // Por ahora, crear días laborales básicos para el año
        val diasDelAño = mutableListOf<DiaLaboral>()
        
        for (mes in 1..12) {
            val diasEnMes = LocalDate.of(año, mes, 1).lengthOfMonth()
            for (dia in 1..diasEnMes) {
                val fecha = LocalDate.of(año, mes, dia)
                val esFinDeSemana = fecha.dayOfWeek.value >= 6 // Sábado (6) y Domingo (7)
                
                android.util.Log.d("DiaLaboralRepo", "Fecha: $fecha, Día: ${fecha.dayOfWeek}, Value: ${fecha.dayOfWeek.value}, EsFinDeSemana: $esFinDeSemana")
                
                val diaLaboral = DiaLaboral(
                    fecha = fecha,
                    esLaboral = !esFinDeSemana, // No laboral si es fin de semana
                    tipoDia = if (esFinDeSemana) DiaLaboral.TipoDia.FIN_DE_SEMANA else DiaLaboral.TipoDia.LABORAL,
                    descripcion = if (esFinDeSemana) {
                        when (fecha.dayOfWeek) {
                            DayOfWeek.SATURDAY -> "Sábado"
                            DayOfWeek.SUNDAY -> "Domingo"
                            else -> "Fin de semana"
                        }
                    } else null
                )
                diasDelAño.add(diaLaboral)
            }
        }
        
        insertDias(diasDelAño)
    }
}