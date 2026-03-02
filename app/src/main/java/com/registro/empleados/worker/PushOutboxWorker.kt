package com.registro.empleados.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.registro.empleados.data.device.DeviceIdentityManager
import com.registro.empleados.data.local.dao.OutboxSubmissionDao
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
@HiltWorker
class PushOutboxWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val deviceIdentityManager: DeviceIdentityManager,
    private val outboxDao: OutboxSubmissionDao,
    private val submissionsApi: SubmissionsApiService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.i("StaffAxis", "PushOutboxWorker START")
        if (!deviceIdentityManager.ensureDeviceToken()) {
            Log.w("StaffAxis", "PushOutboxWorker -> no token, retry later")
            Log.i("StaffAxis", "PushOutboxWorker END result=retry sent=0 failed=0")
            return@withContext Result.retry()
        }
        val pending = outboxDao.getNextPending(limit = BATCH_SIZE)
        Log.i("StaffAxis", "PushOutboxWorker -> outbox count=" + pending.size)
        if (pending.isEmpty()) {
            Log.i("StaffAxis", "PushOutboxWorker END result=success sent=0 failed=0")
            return@withContext Result.success()
        }
        var anySuccess = false
        var anyRetriableFailure = false
        var anyPermanentFailure = false
        var sentCount = 0
        val failedStatusCodes = mutableListOf<Int>()

        for (item in pending) {
            try {
                val request = CreateSubmissionRequestDto(
                    employeeId = item.employeeId,
                    date = item.date,
                    minutesWorked = item.minutesWorked,
                    checkIn = item.checkIn,
                    checkOut = item.checkOut,
                    notes = item.notes
                )
                val response = submissionsApi.createSubmissionRaw(request)
                val code = response.code()
                val contentType = response.headers()["content-type"]
                val bodyString = response.body()?.string().orEmpty()
                val errorString = response.errorBody()?.string().orEmpty()
                val bodyPreview = bodyString.take(300)
                val errorPreview = errorString.take(300)
                Log.i("StaffAxis", "PushOutboxWorker -> createSubmissionRaw code=$code content-type=$contentType body(300)=$bodyPreview error(300)=$errorPreview")
                when (code) {
                    200, 201 -> {
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
                                Log.e("StaffAxis", "PushOutboxWorker permanent failure: HTTP $code error(300)=$errorPreview")
                                outboxDao.markFailedPermanent(item.id, errorMsg)
                                anyPermanentFailure = true
                            }
                        }
                        failedStatusCodes.add(code)
                    }
                    in 500..599 -> {
                        val errorMsg = response.message().takeIf { it?.isNotBlank() == true }
                            ?: "HTTP $code"
                        outboxDao.incrementAttempt(item.id, errorMsg)
                        anyRetriableFailure = true
                        failedStatusCodes.add(code)
                    }
                    else -> {
                        val errorMsg = response.message().takeIf { it?.isNotBlank() == true }
                            ?: "HTTP $code"
                        Log.e("StaffAxis", "PushOutboxWorker permanent failure: HTTP $code error(300)=$errorPreview")
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
                        Log.e("StaffAxis", "PushOutboxWorker -> HttpException code=$code body=$body", e)
                        when (code) {
                            408, 429 -> {
                                anyRetriableFailure = true
                                outboxDao.incrementAttempt(item.id, "HTTP $code")
                            }
                            in 400..499 -> {
                                Log.e("StaffAxis", "PushOutboxWorker permanent failure: HTTP $code body=$body")
                                outboxDao.markFailedPermanent(item.id, "HTTP $code")
                                anyPermanentFailure = true
                            }
                            in 500..599 -> {
                                anyRetriableFailure = true
                                outboxDao.incrementAttempt(item.id, "HTTP $code")
                            }
                            else -> {
                                outboxDao.markFailedPermanent(item.id, "HTTP $code")
                                anyPermanentFailure = true
                            }
                        }
                        "HTTP $code"
                    }
                    else -> {
                        Log.e("StaffAxis", "PushOutboxWorker -> fail", e)
                        outboxDao.incrementAttempt(item.id, e.message ?: e.javaClass.simpleName)
                        anyRetriableFailure = true
                        e.message ?: e.javaClass.simpleName
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
        val statusInfo = if (failedStatusCodes.isEmpty()) "" else " status=${failedStatusCodes.distinct().joinToString(",")}"
        when {
            anyPermanentFailure -> {
                Log.e("StaffAxis", "PushOutboxWorker permanent failure (no retry) sent=$sentCount failed=$failedCount$statusInfo")
                Log.i("StaffAxis", "PushOutboxWorker END result=failure sent=$sentCount failed=$failedCount")
                Result.failure()
            }
            anyRetriableFailure && !anySuccess -> {
                Log.e("StaffAxis", "PushOutboxWorker -> fail sent=$sentCount failed=$failedCount$statusInfo")
                Log.i("StaffAxis", "PushOutboxWorker END result=retry sent=$sentCount failed=$failedCount")
                Result.retry()
            }
            else -> {
                Log.i("StaffAxis", "PushOutboxWorker -> success")
                Log.i("StaffAxis", "PushOutboxWorker END result=success sent=$sentCount failed=$failedCount")
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
        WorkManager.getInstance(context).enqueue(work)
    }

    companion object {
        private const val TAG = "PushOutboxWorker"
        const val WORK_NAME = "push_outbox_worker"
        const val WORK_TAG = "push_outbox"
        private const val BATCH_SIZE = 10

        val BACKOFF = BackoffPolicy.EXPONENTIAL
        val BACKOFF_DELAY = 10L
        val BACKOFF_UNIT = TimeUnit.SECONDS
    }
}
