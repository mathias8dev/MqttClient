package com.mathias8dev.mqttclient.domain.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import timber.log.Timber


class FetcherBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        Timber.i("Broadcast received.")
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
