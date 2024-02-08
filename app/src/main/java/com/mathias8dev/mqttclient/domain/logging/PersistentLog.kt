package com.mathias8dev.mqttclient.domain.logging

import timber.log.Timber

class PersistentLog: Timber.DebugTree() {




    override fun d(message: String?, vararg args: Any?) {
        super.d(message, *args)
    }

    override fun d(t: Throwable?) {
        super.d(t)
    }

    override fun d(t: Throwable?, message: String?, vararg args: Any?) {
        super.d(t, message, *args)
    }

    override fun e(t: Throwable?, message: String?, vararg args: Any?) {
        super.d(t, message, *args)
    }

    override fun e(t: Throwable?) {
        super.e(t)
    }

    override fun e(message: String?, vararg args: Any?) {
        super.e(message, *args)
    }
    override fun i(t: Throwable?, message: String?, vararg args: Any?) {
        super.d(t, message, *args)
    }

    override fun i(t: Throwable?) {
        super.i(t)
    }

    override fun i(message: String?, vararg args: Any?) {
        super.i(message, *args)
    }
    override fun w(t: Throwable?, message: String?, vararg args: Any?) {
        super.d(t, message, *args)
    }

    override fun w(t: Throwable?) {
        super.w(t)
    }

    override fun w(message: String?, vararg args: Any?) {
        super.w(message, *args)
    }
}
