package com.registro.empleados.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.registro.empleados.data.device.DeviceIdentityManager
import com.registro.empleados.data.local.dao.OutboxSubmissionDao
import com.registro.empleados.data.local.preferences.SyncStatePrefs
import com.registro.empleados.data.remote.api.SubmissionsApiService
import com.registro.empleados.data.remote.dto.CreateSubmissionRequestDto
import dagger.assisted.Assisted
import retrofit2.HttpException
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Worker que envía los submissions pendientes del outbox al backend.
 * Requiere red CONNECTED. Reintenta con backoff en fallos.
 */
private const val TEMP_FAIL_COOLDOWN_MS = 60_000L

@HiltWorker
class PushOutboxWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val deviceIdentityManager: DeviceIdentityManager,
    private val outboxDao: OutboxSubmissionDao,
    private val submissionsApi: SubmissionsApiService,
    private val syncStatePrefs: SyncStatePrefs
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (!deviceIdentityManager.ensureDeviceToken()) {
            Log.i("StaffAxis", "OUTBOX_RUN skip (no token/config)")
            return@withContext Result.success()
        }
        val now = System.currentTimeMillis()
        val lastTempFail = syncStatePrefs.getLastTempFailAt()
        if (lastTempFail > 0 && (now - lastTempFail) < TEMP_FAIL_COOLDOWN_MS) {
            Log.i("StaffAxis", "OUTBOX_RUN skip (cooldown after temp fail)")
            return@withContext Result.retry()
        }
        val pending = try {
            outboxDao.getNextPending(limit = BATCH_SIZE)
        } catch (e: Exception) {
            Log.e("SYNC_CRASH", "Error leyendo outbox (Room/DB)", e)
            return@withContext Result.failure()
        }
        Log.i("StaffAxis", "OUTBOX_RUN start count=${pending.size}")
        if (pending.isEmpty()) {
            return@withContext Result.success()
        }
        var anySuccess = false
        var anyRetriableFailure = false
        var anyPermanentFailure = false
        var sentCount = 0
        val failedStatusCodes = mutableListOf<Int>()

        for (item in pending) {
            try {
                val request = try {
                    CreateSubmissionRequestDto(
                        employeeId = item.employeeId,
                        date = item.date,
                        minutesWorked = item.minutesWorked,
                        checkIn = item.checkIn,
                        checkOut = item.checkOut,
                        notes = item.notes
                    )
                } catch (e: Exception) {
                    Log.e("SYNC_CRASH", "Error armando payload (serialización/mapeo)", e)
                    outboxDao.incrementAttempt(item.id, e.message ?: e.javaClass.simpleName)
                    return@withContext Result.failure()
                }
                val response = try {
                    submissionsApi.createSubmissionRaw(request)
                } catch (e: Exception) {
                    if (e is HttpException) throw e
                    Log.e("SYNC_CRASH", "Error enviando payload (red/serialización)", e)
                    outboxDao.incrementAttempt(item.id, e.message ?: e.javaClass.simpleName)
                    return@withContext Result.failure()
                }
                val code = response.code()
                val contentType = response.headers()["content-type"]
                val bodyString = response.body()?.string().orEmpty()
                val errorString = response.errorBody()?.string().orEmpty()
                val errorPreview = errorString.take(300)
                when (code) {
                    200, 201, 202 -> {
                        outboxDao.markSent(item.id)
                        anySuccess = true
                        sentCount++
                    }
                    in 400..499 -> {
                        val errorMsg = response.message().takeIf { it?.isNotBlank() == true }
                            ?: "HTTP $code"
                        when (code) {
                            408, 429 -> {
                                outboxDao.incrementAttempt(item.id, errorMsg)
                                anyRetriableFailure = true
                            }
                            else -> {
                                Log.e("StaffAxis", "OUTBOX_RUN permanent code=$code endpoint=/api/submissions")
                                outboxDao.markFailedPermanent(item.id, errorMsg)
                                anyPermanentFailure = true
                            }
                        }
                        failedStatusCodes.add(code)
                    }
                    503 -> {
                        val bodyStr = bodyString + errorString
                        val is1102 = bodyStr.contains("1102")
                        val errorMsg = if (is1102) "error code: 1102" else (response.message().takeIf { it?.isNotBlank() == true } ?: "HTTP 503")
                        outboxDao.incrementAttempt(item.id, errorMsg)
                        syncStatePrefs.setLastTempFailAt(System.currentTimeMillis())
                        Log.i("StaffAxis", "TEMP_FAIL 503/1102 -> stop batch, schedule retry")
                        return@withContext Result.retry()
                    }
                    in 500..599 -> {
                        val bodyStr = bodyString + errorString
                        val is1102 = bodyStr.contains("1102")
                        if (is1102) {
                            val errorMsg = "error code: 1102"
                            outboxDao.incrementAttempt(item.id, errorMsg)
                            syncStatePrefs.setLastTempFailAt(System.currentTimeMillis())
                            Log.i("StaffAxis", "TEMP_FAIL 503/1102 -> stop batch, schedule retry")
                            return@withContext Result.retry()
                        }
                        val errorMsg = response.message().takeIf { it?.isNotBlank() == true } ?: "HTTP $code"
                        outboxDao.incrementAttempt(item.id, errorMsg)
                        anyRetriableFailure = true
                        failedStatusCodes.add(code)
                    }
                    else -> {
                        val errorMsg = response.message().takeIf { it?.isNotBlank() == true }
                            ?: "HTTP $code"
                        Log.e("StaffAxis", "OUTBOX_RUN permanent code=$code endpoint=/api/submissions")
                        outboxDao.markFailedPermanent(item.id, errorMsg)
                        anyPermanentFailure = true
                        failedStatusCodes.add(code)
                    }
                }
            } catch (e: Exception) {
                val msg = when (e) {
                    is HttpException -> {
                        val code = e.code()
                        val body = e.response()?.errorBody()?.string().orEmpty()
                        Log.e("StaffAxis", "OUTBOX_RUN HttpException code=$code endpoint=/api/submissions", e)
                        when (code) {
                            408, 429, 503 -> {
                                val is1102 = body.contains("1102")
                                val errorMsg = if (code == 503 && is1102) "error code: 1102" else "HTTP $code"
                                outboxDao.incrementAttempt(item.id, errorMsg)
                                if (code == 503 || is1102) {
                                    syncStatePrefs.setLastTempFailAt(System.currentTimeMillis())
                                    Log.i("StaffAxis", "TEMP_FAIL 503/1102 -> stop batch, schedule retry")
                                    return@withContext Result.retry()
                                }
                                anyRetriableFailure = true
                            }
                            in 400..499 -> {
                                Log.e("StaffAxis", "OUTBOX_RUN permanent code=$code endpoint=/api/submissions")
                                outboxDao.markFailedPermanent(item.id, "HTTP $code")
                                anyPermanentFailure = true
                            }
                            in 500..599 -> {
                                val is1102 = body.contains("1102")
                                outboxDao.incrementAttempt(item.id, if (is1102) "error code: 1102" else "HTTP $code")
                                if (is1102) {
                                    syncStatePrefs.setLastTempFailAt(System.currentTimeMillis())
                                    Log.i("StaffAxis", "TEMP_FAIL 503/1102 -> stop batch, schedule retry")
                                    return@withContext Result.retry()
                                }
                                anyRetriableFailure = true
                            }
                    else -> {
                        outboxDao.markFailedPermanent(item.id, "HTTP $code")
                        anyPermanentFailure = true
                            }
                        }
                        "HTTP $code"
                    }
                    else -> {
                        Log.e("SYNC_CRASH", "Error armando/enviando payload", e)
                        outboxDao.incrementAttempt(item.id, e.message ?: e.javaClass.simpleName)
                        return@withContext Result.failure()
                    }
                }
                if (e is HttpException) {
                    failedStatusCodes.add(e.code())
                } else {
                    failedStatusCodes.add(-1)
                }
            }
        }

        val failedCount = pending.size - sentCount
        val remaining = outboxDao.countPending()
        val batchCapReached = pending.size >= BATCH_SIZE && remaining > 0
        when {
            anyPermanentFailure -> {
                Log.i("StaffAxis", "OUTBOX_RUN sent=$sentCount failed=$failedCount tempFail=false remaining=$remaining")
                Result.failure()
            }
            anyRetriableFailure && !anySuccess -> {
                Log.i("StaffAxis", "OUTBOX_RUN sent=$sentCount failed=$failedCount tempFail=false remaining=$remaining")
                Result.retry()
            }
            batchCapReached -> {
                Log.i("StaffAxis", "OUTBOX_RUN sent=$sentCount failed=$failedCount tempFail=false remaining=$remaining BATCH_CAP reached")
                Result.retry()
            }
            else -> {
                Log.i("StaffAxis", "OUTBOX_RUN sent=$sentCount failed=$failedCount tempFail=false remaining=$remaining")
                if (sentCount > 0) {
                    schedulePullApproved()
                }
                Result.success()
            }
        }
    }

    private fun schedulePullApproved() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()
        val work = OneTimeWorkRequestBuilder<PullApprovedWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                PullApprovedWorker.BACKOFF_DELAY,
                PullApprovedWorker.BACKOFF_UNIT
            )
            .addTag(PullApprovedWorker.WORK_TAG)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            PullApprovedWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            work
        )
    }

    companion object {
        private const val TAG = "PushOutboxWorker"
        const val WORK_NAME = "push_outbox"
        const val WORK_TAG = "push_outbox"
        private const val BATCH_SIZE = 10

        val BACKOFF = BackoffPolicy.EXPONENTIAL
        val BACKOFF_DELAY = 30L
        val BACKOFF_UNIT = TimeUnit.SECONDS
    }
}
