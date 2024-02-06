package com.mathias8dev.mqttclient.storage.dto

import com.google.gson.annotations.SerializedName
import com.mathias8dev.mqttclient.storage.room.model.Measurement


data class MeasurementDto(
    var temperature: Float = 0f,
    var humidity: Float = 0f,
    var co2: Float = 0f,
    @SerializedName("pH")
    var ph: Float = 0f,
    @SerializedName("alcool")
    var alcohol: Float = 0f
)

fun MeasurementDto.toMeasurement() = Measurement(
    temperature = temperature,
    humidity = humidity,
    co2 = co2,
    ph = ph,
    alcohol = alcohol
)