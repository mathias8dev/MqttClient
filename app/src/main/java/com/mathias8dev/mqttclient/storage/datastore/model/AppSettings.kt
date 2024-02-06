package com.mathias8dev.mqttclient.storage.datastore.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class AppSettings(
    val selectedConfigId: Long = -1
): Parcelable
