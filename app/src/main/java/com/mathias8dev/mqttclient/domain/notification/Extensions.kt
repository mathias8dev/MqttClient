package com.mathias8dev.mqttclient.domain.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.UUID


@RequiresApi(Build.VERSION_CODES.N)
fun Context.createNotificationChannel(
    channelId: String = UUID.randomUUID().toString(),
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT, // From android 8
    @StringRes channelNameRes: Int,
    @StringRes channelDescriptionRes: Int,
) {
    createNotificationChannel(
        channelId = channelId,
        importance = importance,
        channelName = getString(channelNameRes),
        channelDescription = getString(channelDescriptionRes)
    )
}


@RequiresApi(Build.VERSION_CODES.N)
fun Context.createNotificationChannel(
    channelId: String = UUID.randomUUID().toString(),
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    channelName: String,
    channelDescription: String
) {

    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is not in the Support Library.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun Context.notify(
    notificationId: Int,
    builder: NotificationCompat.Builder
) {
    notify(this, builder.build(), notificationId)
}

@RequiresApi(Build.VERSION_CODES.N)
fun Context.notify(
    notificationData: NotificationData,
    channelId: String = UUID.randomUUID().toString(),
    groupKey: String? = null,
    onAddMoreCustomization: ((builder: NotificationCompat.Builder) -> Unit)? = null
) {

    val notificationBuilder = if (notificationData is NotificationDataRes)
        notificationData.toNotificationBuilder(this, channelId)
    else (notificationData as NotificationDataRaw).toNotificationBuilder(this, channelId)

    val notification = notificationBuilder.apply {
        groupKey?.let { this.setGroup(it) }
        onAddMoreCustomization?.invoke(this)
    }.build()

    notify(this, notification, notificationData.id)
}

@RequiresApi(Build.VERSION_CODES.N)
fun Context.notify(
    notificationData: NotificationDataRes,
    channelId: String = UUID.randomUUID().toString(),
    groupKey: String? = null,
    onAddMoreCustomization: ((builder: NotificationCompat.Builder) -> Unit)? = null
) {

    val notification = notificationData.toNotificationBuilder(this, channelId)
        .apply {
            groupKey?.let { this.setGroup(it) }
            onAddMoreCustomization?.invoke(this)
        }
        .build()
    notify(this, notification, notificationData.id)
}

internal fun notify(
    context: Context,
    notification: Notification,
    notificationId: Int,
) {

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notify(
                notificationId,
                notification
            )
        }

    }
}

fun NotificationDataRes.toNotificationBuilder(
    context: Context,
    channelId: String
): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, channelId)
        .setSmallIcon(iconRes)
        .setContentTitle(context.getString(titleRes))
        .setContentText(context.getString(contentRes))
        .setPriority(priority)
        .apply {
            actionIntent?.let {
                setContentIntent(actionIntent)
            }
        }
        .setAutoCancel(true)
        .setVisibility(
            if (makeVisibleOnLockScreen) NotificationCompat.VISIBILITY_PUBLIC
            else NotificationCompat.VISIBILITY_PRIVATE
        ).setShowWhen(showWhen).apply {
            moreContentRes?.let {
                this.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(context.getString(it))
                )
            }
        }
}

fun NotificationDataRaw.toNotificationBuilder(
    context: Context,
    channelId: String
): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, channelId)
        .setSmallIcon(iconRes)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(priority)
        .apply {
            actionIntent?.let {
                setContentIntent(actionIntent)
            }
        }
        .setAutoCancel(true)
        .setVisibility(
            if (makeVisibleOnLockScreen) NotificationCompat.VISIBILITY_PUBLIC
            else NotificationCompat.VISIBILITY_PRIVATE
        ).setShowWhen(showWhen).apply {
            moreContent?.let {
                this.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(it)
                )
            }
        }


}