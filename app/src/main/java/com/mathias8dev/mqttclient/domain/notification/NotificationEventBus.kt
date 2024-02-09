package com.mathias8dev.mqttclient.domain.notification

import androidx.core.app.NotificationCompat
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.coroutineContext

object NotificationEventBus {
    private val _events = MutableSharedFlow<NotificationEvent>()
    val events = _events.asSharedFlow()

    suspend fun publish(event: NotificationEvent) {
        _events.emit(event)
    }

    suspend inline fun <reified T: NotificationEvent> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }
}

sealed class NotificationEvent {
    sealed class Notify(
        val data: NotificationData,
        val onAddMoreCustomization: ((builder: NotificationCompat.Builder) -> Unit)? = null
    ): NotificationEvent()

    class NotifyDefault(
        data: NotificationData,
        onAddMoreCustomization: ((builder: NotificationCompat.Builder) -> Unit)? = null
    ): Notify(data, onAddMoreCustomization)

    data object RequestNotificationPermission : NotificationEvent()
}