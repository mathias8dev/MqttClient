package com.mathias8dev.mqttclient.domain.event

import com.mathias8dev.mqttclient.storage.room.model.Config

abstract class Event


abstract class MQTTClientEvent: Event() {

    data object IDle: MQTTClientEvent()
    data object MQTTClientDisconnectedFromServer: MQTTClientEvent()
    data object MQTTClientConnectedToServer: MQTTClientEvent()
    data class MQTTClientException(
        val config: Config?,
        val cause: Throwable
    ): MQTTClientEvent()
}