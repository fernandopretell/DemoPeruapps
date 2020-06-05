package com.fpretell.demoperuapps.workers

import androidx.work.*
import com.fpretell.demoperuapps.ApplicationClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.concurrent.TimeUnit

private const val ACKNOWLEDGE_PUSH_WORKER = "acknowledgeWorker"
private const val CLEAN_UP_WORKER = "cleanUpWorker"

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class ActivateSaveLocationWorker {

    fun initWorkManager(context: ApplicationClass, workerTimeProvider: WorkerTimeProvider) {
        val work = PeriodicWorkRequestBuilder<SaveLocationWorker>(
            workerTimeProvider.getWorkerTimeModel().minTimeToSync, TimeUnit.MINUTES,
            workerTimeProvider.getWorkerTimeModel().maxTimeToSync, TimeUnit.MINUTES)
            .setConstraints(getWorkerConstraints())
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(ACKNOWLEDGE_PUSH_WORKER,
            ExistingPeriodicWorkPolicy.REPLACE,
            work)
    }

    private fun getWorkerConstraints() = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiresCharging(true)
        .setRequiresDeviceIdle(true)
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

}