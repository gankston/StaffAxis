package com.registro.empleados.data.database.entities

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * Entidad que representa un día laboral o feriado.
 * Se utiliza para determinar si un día específico es laboral o no.
 */
@Entity(tableName = "dias_laborales")
data class DiaLaboral(
    @PrimaryKey
    @ColumnInfo(name = "fecha")
    val fecha: String, // Formato "yyyy-MM-dd"

    @ColumnInfo(name = "es_laboral")
    val esLaboral: Boolean,  // true = día laboral, false = feriado/fin de semana

    @ColumnInfo(name = "tipo_dia")
    val tipoDia: String,  // "LABORAL", "FERIADO", "FIN_DE_SEMANA"

    @ColumnInfo(name = "descripcion")
    val descripcion: String? = null,  // Ej: "Día de la Independencia"

    @ColumnInfo(name = "fecha_actualizacion")
    val fechaActualizacion: Long = System.currentTimeMillis()
)
