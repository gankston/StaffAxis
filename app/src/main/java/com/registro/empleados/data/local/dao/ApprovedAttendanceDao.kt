package com.registro.empleados.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.registro.empleados.data.local.entity.ApprovedAttendanceEntity

/**
 * DAO para attendances aprobados (backénd). No se usa en la UI actual.
 * Para mostrar "oficial": exponer queries por sector_id, date, etc. desde un Repository.
 */
@Dao
interface ApprovedAttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ApprovedAttendanceEntity)

    @Query("UPDATE approved_attendances SET is_deleted = 1 WHERE id = :id")
    suspend fun markDeleted(id: String)
}
