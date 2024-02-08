package com.mathias8dev.mqttclient.domain.event

import com.mathias8dev.mqttclient.storage.room.model.Config

abstract class Event

abstract class MQTTClientEvent: Event() {

    data object IDLE: MQTTClientEvent()
    data object MQTTClientDisconnectedFromServer: MQTTClientEvent()
    data object MQTTClientConnectedToServer: MQTTClientEvent()

    abstract class MQTTClientException(
        open val config: Config?,
        open val cause: Throwable
    ): MQTTClientEvent()
    data class MQTTClientInitializationException(
        override val config: Config?,
        override val cause: Throwable
    ): MQTTClientException(config, cause)

    data class MQTTClientConnectException(
        override val config: Config?,
        override val cause: Throwable
    ): MQTTClientException(config, cause)
    data class MQTTClientTopicSubscriptionException(
        override val config: Config?,
        override val cause: Throwable
    ): MQTTClientException(config, cause)

    data class MQTTClientDisconnectException(
        override val config: Config?,
        override val cause: Throwable
    ): MQTTClientException(config, cause)
}
