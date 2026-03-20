package com.registro.empleados.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cola outbox para submissions pendientes de envío al backend.
 * Permite reintentos y tracking de errores.
 */
@Entity(
    tableName = "outbox_submissions",
    indices = [androidx.room.Index(value = ["dedup_key"], unique = true)]
)
data class OutboxSubmissionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID

    @ColumnInfo(name = "dedup_key")
    val dedupKey: String? = null,

    @ColumnInfo(name = "employee_id")
    val employeeId: String,

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

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "attempts")
    val attempts: Int = 0,

    @ColumnInfo(name = "last_error")
    val lastError: String? = null,

    @ColumnInfo(name = "status")
    val status: String // 'pending' | 'sent' | 'failed_permanent'
)
