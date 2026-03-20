package com.registro.empleados.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "ausencia_table")
data class AusenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val legajoEmpleado: String,
    val nombreEmpleado: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val motivo: String?,
    val observaciones: String? = null,
    val esJustificada: Boolean = false,
    val fechaCreacion: LocalDate = LocalDate.now(),
    val syncStatus: String = "pending",
    val attempts: Int = 0,
    val lastError: String? = null
)
