package com.registro.empleados.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entidad de Room para empleados.
 */
@Entity(tableName = "empleados")
data class EmpleadoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** ID del empleado en el backend (API). Obligatorio para enviar submissions. */
    @ColumnInfo(name = "employee_id_backend")
    val employeeIdBackend: String? = null,
    val legajo: String?,
    val nombreCompleto: String,
    val sector: String,
    val fechaIngreso: LocalDate,
    val activo: Boolean = true,
    val fechaCreacion: LocalDate = LocalDate.now(),
    val observacion: String? = null,
    val dni: String? = null
)
