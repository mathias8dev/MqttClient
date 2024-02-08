package com.mathias8dev.mqttclient.domain.utils

import androidx.compose.runtime.Composable
import com.mathias8dev.mqttclient.storage.room.model.Config


fun <T> T.thenSync(block: ()->Unit) {
    if (this is Boolean && this) block.invoke()
}



suspend fun <T> T.thenAsync(block: suspend ()->Unit) {
    if (this is Boolean && this) block.invoke()
}

fun Config.toMQTTServerUri() = "tcp://$serverUrl:$serverPort"