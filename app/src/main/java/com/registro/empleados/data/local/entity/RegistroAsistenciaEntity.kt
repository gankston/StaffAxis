package com.registro.empleados.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad de Room para registros de asistencia.
 * Nueva estructura: solo horas trabajadas manuales (4, 8 o 12 horas).
 */
@Entity(
    tableName = "registros_asistencia"
)
data class RegistroAsistenciaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "id_empleado")
    val idEmpleado: Long? = null,  // Opcional, sin FK estricta

    // Campo auxiliar para compatibilidad con flujos existentes
    @ColumnInfo(name = "legajo_empleado")
    val legajoEmpleado: String? = null,

    @ColumnInfo(name = "fecha")
    val fecha: String,  // Formato "yyyy-MM-dd" - día que se registran las horas

    @ColumnInfo(name = "horas_trabajadas")
    val horasTrabajadas: Int,  // CAMBIADO: Ahora es Int con valores 4, 8 o 12 únicamente

    @ColumnInfo(name = "observaciones")
    val observaciones: String? = null,

    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Long = System.currentTimeMillis()  // Timestamp de cuándo se cargó el registro
)
