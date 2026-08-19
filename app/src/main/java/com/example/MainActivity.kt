package com.example
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.repository.CommodityRepositoryImpl
import com.example.data.worker.AgriSyncWorker
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.AgriSyncTheme
import com.example.ui.viewmodel.AgriSyncViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val repository by lazy {
        CommodityRepositoryImpl.getInstance(applicationContext)
    }

    private val viewModel: AgriSyncViewModel by viewModels {
        AgriSyncViewModel.provideFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Seed local Room database on first launch (instant offline availability)
        lifecycleScope.launch {
            repository.initializeSeedDataIfNeeded()
        }

        // Schedule guaranteed background sync via WorkManager
        enqueuePeriodicSync()

        setContent {
            AgriSyncTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }

    /**
     * Enqueues a periodic background sync using WorkManager.
     *
     * Configuration:
     * - Repeats every 6 hours (minimum for PeriodicWorkRequest is 15 min)
     * - Requires network connectivity (WiFi or cellular)
     * - Uses EXPONENTIAL backoff starting at 30 seconds on failure
     * - KEEP policy: if a sync is already scheduled, don't replace it
     *
     * This ensures fresh APMC market data even when the app is not
     * in the foreground — critical for farmers in low-connectivity zones
     * who may open the app intermittently.
     */
    private fun enqueuePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<AgriSyncWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 30,
                timeUnit = TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            AgriSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        Log.d(TAG, "WorkManager periodic sync enqueued: every 6h, network-constrained, exponential backoff")
    }
}
