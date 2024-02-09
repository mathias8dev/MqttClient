package com.mathias8dev.mqttclient.domain.receiver


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mathias8dev.mqttclient.R
import com.mathias8dev.mqttclient.domain.notification.NotificationDataRaw
import com.mathias8dev.mqttclient.domain.notification.NotificationUtils
import com.mathias8dev.mqttclient.domain.notification.toNotificationBuilder
import com.mathias8dev.mqttclient.domain.service.MeasurementDataFetchService
import com.mathias8dev.mqttclient.domain.service.ServiceLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainCoroutineWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val notification = NotificationDataRaw(
        notificationId,
        iconRes = R.drawable.ic_launcher_background,
        content = "Ne fermez pas l'application",
        title = "MQTTClient"
    ).toNotificationBuilder(this.context, NotificationUtils.Channel.Default.channelId).build()

    override suspend fun doWork(): Result {
        // do not launch if the service is already alive
        if (MeasurementDataFetchService.currentService == null) {
            withContext(Dispatchers.IO) {
                ServiceLauncher.launchService(context, MeasurementDataFetchService::class.java)
            }
        }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            notificationId,
            notification
        )
    }


    companion object {
        private const val notificationId = 9973
    }
}