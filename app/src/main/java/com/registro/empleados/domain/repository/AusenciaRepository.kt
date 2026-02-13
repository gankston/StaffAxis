package com.registro.empleados.domain.repository

import com.registro.empleados.domain.model.Ausencia
import java.time.LocalDate

interface AusenciaRepository {
    suspend fun insertAusencia(ausencia: Ausencia): Long
    suspend fun updateAusencia(ausencia: Ausencia)
    suspend fun deleteAusencia(ausencia: Ausencia)
    suspend fun getAusenciasByLegajo(legajo: String): List<Ausencia>
    suspend fun getAusenciasByFecha(fecha: LocalDate): List<Ausencia>
    suspend fun getAusenciasByRango(fechaInicio: LocalDate, fechaFin: LocalDate): List<Ausencia>
    suspend fun empleadoTieneAusenciaEnFecha(legajo: String, fecha: LocalDate): Boolean
    suspend fun getAllAusencias(): List<Ausencia>
}
