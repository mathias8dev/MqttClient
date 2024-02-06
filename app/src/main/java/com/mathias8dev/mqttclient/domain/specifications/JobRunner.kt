package com.mathias8dev.mqttclient.domain.specifications

interface JobRunner {

    fun doJob()

    fun stopJob()
}