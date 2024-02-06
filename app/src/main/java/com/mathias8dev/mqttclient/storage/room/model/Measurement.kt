package com.mathias8dev.mqttclient.storage.room.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.mathias8dev.mqttclient.storage.dto.MeasurementDto
import kotlinx.parcelize.Parcelize



@Parcelize
@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val co2: Float = 0f,
    @SerializedName("pH")
    val ph: Float = 0f,
    @SerializedName("alcool")
    val alcohol: Float = 0f
): Parcelable

fun Measurement.toMeasurementDto() = MeasurementDto(
    temperature = temperature,
    humidity = humidity,
    co2 = co2,
    ph = ph,
    alcohol = alcohol
)