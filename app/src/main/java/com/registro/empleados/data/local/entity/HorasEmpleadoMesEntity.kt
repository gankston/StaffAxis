package com.registro.empleados.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.LocalDate

/**
 * Entidad de Room para horas trabajadas por empleado por mes.
 */
@Entity(
    tableName = "horas_empleado_mes",
    indices = [Index(value = ["legajoEmpleado"])]
)
data class HorasEmpleadoMesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val legajoEmpleado: String,  // FK a Empleado usando legajo
    val año: Int,
    val mes: Int,
    val totalHoras: Double,
    val diasTrabajados: Int,
    val promedioDiario: Double,
    val ultimaActualizacion: LocalDate = LocalDate.now()
)
