package com.mathias8dev.mqttclient.domain.logging

import android.util.Log
import com.mathias8dev.mqttclient.domain.event.EventBus
import com.mathias8dev.mqttclient.domain.event.LoggingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.coroutines.CoroutineContext

class PersistentLog : Timber.DebugTree() {

    @get:JvmSynthetic // Hide from public API.
    internal val explicitTag = ThreadLocal<String>()

    @get:JvmSynthetic // Hide from public API.
    internal val tag: String?
        get() {
            val tag = explicitTag.get()
            if (tag != null) {
                explicitTag.remove()
            }
            return tag
        }


    override fun d(message: String?, vararg args: Any?) {
        super.d(message, *args)
        preparePublish(Log.DEBUG, null, message, *args)
    }

    override fun d(t: Throwable?) {
        super.d(t)
        preparePublish(Log.DEBUG, t, null, null)
    }

    override fun d(t: Throwable?, message: String?, vararg args: Any?) {
        super.d(t, message, *args)
        preparePublish(Log.DEBUG, t, message, *args)
    }

    override fun e(t: Throwable?, message: String?, vararg args: Any?) {
        super.d(t, message, *args)
        preparePublish(Log.ERROR, t, message, *args)
    }

    override fun e(t: Throwable?) {
        super.e(t)
        preparePublish(Log.ERROR, t, null, null)
    }

    override fun e(message: String?, vararg args: Any?) {
        super.e(message, *args)
        preparePublish(Log.ERROR, null, message, *args)
    }
    override fun i(t: Throwable?, message: String?, vararg args: Any?) {
        super.d(t, message, *args)
        preparePublish(Log.INFO, t, message, *args)
    }

    override fun i(t: Throwable?) {
        super.i(t)
        preparePublish(Log.INFO, t, null, null)
    }

    override fun i(message: String?, vararg args: Any?) {
        super.i(message, *args)
        preparePublish(Log.INFO, null, message, *args)
    }
    override fun w(t: Throwable?, message: String?, vararg args: Any?) {
        super.d(t, message, *args)
        preparePublish(Log.WARN, t, message, *args)
    }

    override fun w(t: Throwable?) {
        super.w(t)
        preparePublish(Log.WARN, t, null)
    }

    override fun w(message: String?, vararg args: Any?) {
        super.w(message, *args)
        preparePublish(Log.WARN, null, message, *args)
    }

    private fun preparePublish(priority: Int, t: Throwable?, message: String?, vararg args: Any?) {
        // Consume tag even when message is not loggable so that next message is correctly tagged.
        val tag = tag
        if (!isLoggable(tag, priority)) {
            return
        }

        var message = message
        if (message.isNullOrEmpty()) {
            if (t == null) {
                return  // Swallow message if it's null and there's no throwable.
            }
            message = getStackTraceString(t)
        } else {
            if (args.isNotEmpty()) {
                message = formatMessage(message, args)
            }
            if (t != null) {
                message += "\n" + getStackTraceString(t)
            }
        }

        publish(priority, tag, message, t)
    }

    private fun publish(priority: Int, tag: String?, message: String, t: Throwable?) {
        CoroutineScope(Dispatchers.IO).launch {
            EventBus.publish(
                LoggingEvent(
                    priority = priority,
                    tag = tag,
                    message = message,
                    throwable = t
                )
            )
        }

    }

    private fun getStackTraceString(t: Throwable): String {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }
}
