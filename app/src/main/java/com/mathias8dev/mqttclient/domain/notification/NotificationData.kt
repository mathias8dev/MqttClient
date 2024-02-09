package com.mathias8dev.mqttclient.domain.notification

import android.app.PendingIntent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat

abstract class NotificationData(
    open val id: Int,
    @DrawableRes open val iconRes: Int,
    open val priority: Int = NotificationCompat.PRIORITY_DEFAULT, // Bellow android 8
    open val makeVisibleOnLockScreen: Boolean = false,
    open val showWhen: Boolean = false,
    open val actionIntent: PendingIntent? = null
)

data class NotificationDataRes(
    override val id: Int,
    @DrawableRes override val iconRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val contentRes: Int,
    @StringRes val moreContentRes: Int? = null,
    override val priority: Int = NotificationCompat.PRIORITY_DEFAULT, // Bellow android 8
    override val makeVisibleOnLockScreen: Boolean = false,
    override val showWhen: Boolean = false,
    override val actionIntent: PendingIntent? = null
): NotificationData(
    id,
    iconRes,
    priority,
    makeVisibleOnLockScreen,
    showWhen,
    actionIntent
)

data class NotificationDataRaw(
    override val id: Int,
    @DrawableRes override val iconRes: Int,
    val title: String,
    val content: String,
    val moreContent: String? = null,
    override val priority: Int = NotificationCompat.PRIORITY_DEFAULT, // Bellow android 8
    override val makeVisibleOnLockScreen: Boolean = false,
    override val showWhen: Boolean = false,
    override val actionIntent: PendingIntent? = null
): NotificationData(
    id,
    iconRes,
    priority,
    makeVisibleOnLockScreen,
    showWhen,
    actionIntent
)