package com.registro.empleados.data.database.entities

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entidad que representa un registro de asistencia de un empleado.
 * Incluye entrada, salida y horas trabajadas calculadas.
 */
@Entity(
    tableName = "registros_asistencia",
    foreignKeys = [
        ForeignKey(
            entity = Empleado::class,
            parentColumns = ["legajo"],
            childColumns = ["legajo_empleado"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["legajo_empleado"])]
)
data class RegistroAsistencia(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "legajo_empleado")
    val legajoEmpleado: String,  // FK a Empleado

    @ColumnInfo(name = "fecha")
    val fecha: String,  // Formato "yyyy-MM-dd"

    @ColumnInfo(name = "hora_entrada")
    val horaEntrada: Long?,  // Timestamp o null si no registró entrada

    @ColumnInfo(name = "hora_salida")
    val horaSalida: Long?,  // Timestamp o null si no registró salida

    @ColumnInfo(name = "horas_trabajadas")
    val horasTrabajadas: Double?,  // Calculado: (salida - entrada) en horas

    @ColumnInfo(name = "observaciones")
    val observaciones: String? = null
)
