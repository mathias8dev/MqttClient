package com.mathias8dev.mqttclient.domain.receiver


import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mathias8dev.mqttclient.R
import com.mathias8dev.mqttclient.domain.notification.ServiceNotification
import com.mathias8dev.mqttclient.domain.service.MeasurementDataFetchService
import com.mathias8dev.mqttclient.domain.service.ServiceLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


class MainCoroutineWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {



    override suspend fun doWork(): Result {
        // do not launch if the service is already alive
        if (MeasurementDataFetchService.currentService == null) {
            withContext(Dispatchers.IO) {

                ServiceNotification.notificationText = "do not close the app, please"
                ServiceNotification.notificationIcon = R.drawable.ic_launcher_background
                ServiceLauncher.launchService(context, MeasurementDataFetchService::class.java)
            }
        }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        ServiceNotification.notificationText = "do not close the app, please"
        ServiceNotification.notificationIcon = R.drawable.ic_launcher_background
        val notification = ServiceNotification(context, NOTIFICATION_ID, true)
        return ForegroundInfo(NOTIFICATION_ID, notification.notification!!)
    }


    companion object {
        private var NOTIFICATION_ID = 9973
    }
}