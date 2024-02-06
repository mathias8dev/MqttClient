package com.mathias8dev.mqttclient.domain.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import timber.log.Timber


object ServiceLauncher {


    fun <T: Service> launchService(context: Context, clazz: Class<T>) {

        Intent(context, clazz).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
            } else {
                context.startService(it)
            }
        }

        Timber.d("launchService:  Service is starting....")
    }


}