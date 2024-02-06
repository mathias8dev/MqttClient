package com.mathias8dev.mqttclient.domain.utils

import com.mathias8dev.mqttclient.storage.room.model.Config


fun <T> T.thenSync(block: ()->Unit) {
    block.invoke()
}

suspend fun <T> T.thenAsync(block: suspend ()->Unit) {
    block.invoke()
}

fun Config.toMQTTServerUri() = "tcp://$serverUrl:$serverPort"