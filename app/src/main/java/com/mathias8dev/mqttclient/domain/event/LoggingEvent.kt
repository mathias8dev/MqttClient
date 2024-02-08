package com.mathias8dev.mqttclient.domain.event


import androidx.compose.runtime.compositionLocalOf
import java.util.Date



data class LoggingEvent(
    val priority: Int,
    val tag: String?,
    val message: String,
    val throwable: Throwable?,
    val createdAt: Date = Date()
): Event()

val LocalLoggingEvents = compositionLocalOf { emptyList<LoggingEvent>() }