package com.mathias8dev.mqttclient.storage.room.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.mathias8dev.mqttclient.storage.dto.MeasurementDto
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val co2: Float = 0f,
    @SerializedName("pH") val ph: Float = 0f,
    @SerializedName("alcool") val alcohol: Float = 0f
) : Parcelable


class MeasurementChartModels(
    val humidity: CartesianChartModel?,
    val temperature: CartesianChartModel?,
    val co2: CartesianChartModel?,
    val ph: CartesianChartModel?,
    val alcohol: CartesianChartModel?,
)


fun Measurement.toMeasurementDto() = MeasurementDto(
    temperature = temperature, humidity = humidity, co2 = co2, ph = ph, alcohol = alcohol
)

fun Collection<Measurement>.toChartModels(): MeasurementChartModels {
    return MeasurementChartModels(
        temperature = if (this.isEmpty()) null else CartesianChartModel(LineCartesianLayerModel.build {
            series(*map { it.temperature }.toTypedArray())
        }),
        humidity = if (this.isEmpty()) null else CartesianChartModel(LineCartesianLayerModel.build {
            series(*map { it.humidity }.toTypedArray())
        }),
        co2 = if (this.isEmpty()) null else CartesianChartModel(LineCartesianLayerModel.build {
            series(*map { it.co2 }.toTypedArray())
        }),
        ph = if (this.isEmpty()) null else CartesianChartModel(LineCartesianLayerModel.build {
            series(*map { it.ph }.toTypedArray())
        }),
        alcohol = if (this.isEmpty()) null else CartesianChartModel(LineCartesianLayerModel.build {
            series(*map { it.alcohol }.toTypedArray())
        }),
    )
}