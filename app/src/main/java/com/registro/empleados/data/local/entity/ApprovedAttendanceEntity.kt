package com.registro.empleados.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Espejo local de attendances aprobados del backend (GET /approved).
 * Tabla separada para no alterar registros_asistencia ni el flujo de exportación.
 *
 * La UI actual lee de registros_asistencia (tabla existente). Esta tabla solo
 * almacena lo sincronizado del backend para una futura etapa.
 *
 * DONDE CONECTAR PARA MOSTRAR "OFICIAL":
 * - Crear ApprovedAttendanceRepository con métodos por sector, fecha, etc.
 * - En el ViewModel/pantalla que deba mostrar "asistencias oficiales aprobadas",
 *   inyectar ese repository y exponer Flow/State de approved_attendances
 *   (filtrando is_deleted = 0).
 * - Opcional: mergear con registros locales o mostrar en pestaña/vista separada.
 */
@Entity(tableName = "approved_attendances")
data class ApprovedAttendanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID del backend

    @ColumnInfo(name = "employee_id")
    val employeeId: String,

    @ColumnInfo(name = "sector_id")
    val sectorId: String,

    @ColumnInfo(name = "date")
    val date: String, // YYYY-MM-DD

    @ColumnInfo(name = "minutes_worked")
    val minutesWorked: Int? = null,

    @ColumnInfo(name = "check_in")
    val checkIn: String? = null,

    @ColumnInfo(name = "check_out")
    val checkOut: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Int = 0 // 0 = activo, 1 = soft deleted
)
