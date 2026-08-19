package com.example.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.repository.CommodityRepositoryImpl

/**
 * AgriSyncWorker — Guaranteed Background Sync via WorkManager
 *
 * Responsibilities:
 * 1. Trigger Bright Data DCA batch collection asynchronously.
 * 2. Poll for dataset readiness with exponential backoff.
 * 3. Atomically replace Room cache with fresh scraped data.
 * 4. Return [Result.retry] on transient failures to leverage
 *    WorkManager's built-in exponential backoff policy.
 *
 * This ensures market price data stays fresh even when the app is
 * backgrounded or killed — critical for low-connectivity rural use cases.
 *
 * Scheduling: Enqueued from [com.example.MainActivity] as a periodic
 * work request with [NetworkType.CONNECTED] constraint.
 */
class AgriSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "agrisync_periodic_market_sync"
        private const val TAG = "AgriSyncWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "WorkManager sync started — attempt #$runAttemptCount")

        val repository = CommodityRepositoryImpl.getInstance(applicationContext)

        return try {
            val syncResult = repository.syncCommoditiesFromBrightData(forceRefresh = true)
            syncResult.fold(
                onSuccess = { count ->
                    Log.d(TAG, "Sync successful — $count commodities refreshed from DCA pipeline")
                    Result.success()
                },
                onFailure = { error ->
                    Log.w(TAG, "Sync failed: ${error.message}")
                    if (runAttemptCount < 3) {
                        // Transient failure — let WorkManager retry with its backoff policy
                        Log.d(TAG, "Scheduling retry (attempt ${runAttemptCount + 1})")
                        Result.retry()
                    } else {
                        // Exhausted retries — fail gracefully; seed data remains in Room
                        Log.e(TAG, "Max retries exhausted — falling back to cached seed data")
                        Result.failure()
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during sync", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
