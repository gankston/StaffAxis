package com.registro.empleados.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entidad de Room para días laborales.
 */
@Entity(tableName = "dias_laborales")
data class DiaLaboralEntity(
    @PrimaryKey
    val fecha: LocalDate,
    val esLaboral: Boolean = true,
    val tipoDia: String, // "LABORAL", "FERIADO", "FIN_DE_SEMANA"
    val descripcion: String? = null
)
