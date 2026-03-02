package com.registro.empleados.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.registro.empleados.data.local.entity.OutboxSubmissionEntity

/**
 * DAO para outbox de submissions. Status: pending | sent | failed_permanent.
 * failed_permanent NUNCA se reenvía: getNextPending y countPendingWithSameKey
 * excluyen status != 'pending'. Solo resetToPendingForRetry (acción explícita) lo reactiva.
 */
@Dao
interface OutboxSubmissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(outbox: OutboxSubmissionEntity)

    /** Pendientes a enviar. Excluye failed_permanent. */
    @Query("""
        SELECT * FROM outbox_submissions 
        WHERE status = 'pending' 
        ORDER BY created_at ASC 
        LIMIT :limit
    """)
    suspend fun getNextPending(limit: Int = 10): List<OutboxSubmissionEntity>

    @Query("""
        UPDATE outbox_submissions 
        SET status = 'sent' 
        WHERE id = :id
    """)
    suspend fun markSent(id: String)

    @Query("""
        UPDATE outbox_submissions 
        SET attempts = attempts + 1, last_error = :error 
        WHERE id = :id
    """)
    suspend fun incrementAttempt(id: String, error: String)

    @Query("""
        UPDATE outbox_submissions 
        SET status = 'failed_permanent', last_error = :error 
        WHERE id = :id
    """)
    suspend fun markFailedPermanent(id: String, error: String)

    /**
     * Reactiva un item failed_permanent para reintento manual. Solo llamar desde UI explícita.
     */
    @Query("""
        UPDATE outbox_submissions 
        SET status = 'pending', last_error = NULL 
        WHERE id = :id AND status = 'failed_permanent'
    """)
    suspend fun resetToPendingForRetry(id: String)

    /**
     * Cuenta pendientes con la misma clave (evitar duplicados).
     * Excluye failed_permanent.
     * Usa '' para checkIn/checkOut null y -1 para minutesWorked null.
     */
    @Query("""
        SELECT COUNT(*) FROM outbox_submissions 
        WHERE status = 'pending' 
        AND employee_id = :employeeId 
        AND date = :date 
        AND (COALESCE(check_in, '') = :checkInOrEmpty)
        AND (COALESCE(check_out, '') = :checkOutOrEmpty)
        AND (COALESCE(minutes_worked, -1) = :minutesWorkedOrSentinel)
    """)
    suspend fun countPendingWithSameKey(
        employeeId: String,
        date: String,
        checkInOrEmpty: String,
        checkOutOrEmpty: String,
        minutesWorkedOrSentinel: Int
    ): Int
}
