package com.mathias8dev.mqttclient.domain.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.mathias8dev.mqttclient.domain.notification.NotificationUtils
import timber.log.Timber


class FetcherBroadcastReceiver : BroadcastReceiver() {


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReceive(context: Context, intent: Intent) {
        Timber.i("Broadcast received.")
        NotificationUtils.initDefaultChannel(context)
        startWorker(context)
    }


    companion object {
        fun startWorker(context: Context) {
            val constraints = Constraints.Builder()
                .build()
            val request = OneTimeWorkRequestBuilder<MainCoroutineWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(constraints)
                .build()
            Timber.d( "starting!")
            WorkManager.getInstance(context)
                .enqueue(request)
        }
    }
}
