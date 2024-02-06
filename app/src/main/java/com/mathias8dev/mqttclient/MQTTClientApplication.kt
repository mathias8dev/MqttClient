package com.mathias8dev.mqttclient

import android.app.Application
import android.content.Intent
import android.util.Log
import com.mathias8dev.mqttclient.domain.utils.Strings
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class MQTTClientApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        val intent = Intent()
        intent.action = Strings.LAUNCH_JOB_INTENT_ACTION

        Log.d(MQTTClientApplication::class.java.simpleName, "Sending broadcast")
        sendBroadcast(intent)
    }

}