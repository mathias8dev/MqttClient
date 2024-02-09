package com.mathias8dev.mqttclient.domain.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.mathias8dev.mqttclient.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object NotificationUtils {


    @RequiresApi(Build.VERSION_CODES.N)
    fun initDefaultChannel(context: Context) {
        context.createNotificationChannel(
            channelId = Channel.Default.channelId,
            channelName = context.getString(Channel.Default.nameRes),
            channelDescription = context.getString(Channel.Default.descriptionRes)
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun listenToNotifyEvents(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            asyncListenToNotifyEvents(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun asyncListenToNotifyEvents(context: Context) {
        NotificationEventBus.subscribe<NotificationEvent.Notify> {
            when (it) {
                is NotificationEvent.NotifyDefault -> {
                    context.notify(
                        channelId = Channel.Default.channelId,
                        notificationData = it.data,
                        onAddMoreCustomization = it.onAddMoreCustomization
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun notify(
        context: Context,
        channel: Channel,
        notificationData: NotificationData,
        onAddMoreCustomization: ((builder: NotificationCompat.Builder) -> Unit)? = null
    ) {
        context.notify(
            notificationData,
            channel.channelId,
            onAddMoreCustomization = onAddMoreCustomization
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun notify(
        context: Context,
        notificationId: Int,
        builder: NotificationCompat.Builder
    ) {
        notify(context, builder.build(), notificationId)
    }

    enum class Channel(
        val channelId: String,
        @StringRes val nameRes: Int,
        @StringRes val descriptionRes: Int
    ) {
        Default(
            "MQTTClientDefaultChannel",
            R.string.notification_name,
            R.string.notification_description
        )
    }
}