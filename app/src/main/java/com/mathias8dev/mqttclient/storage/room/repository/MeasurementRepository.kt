package com.mathias8dev.mqttclient.storage.room.repository

import com.mathias8dev.mqttclient.storage.room.dao.MeasurementDao
import com.mathias8dev.mqttclient.storage.room.model.Measurement
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MeasurementRepository @Inject constructor(
    private val measurementDao: MeasurementDao
) {

    fun getAllMeasurements(): Flow<List<Measurement>> {
        return measurementDao.getAllMeasurements()
    }

    suspend fun insertMeasurement(measurement: Measurement) {
        measurementDao.insertMeasurement(measurement)
    }

    suspend fun deleteMeasurement(measurement: Measurement) {
        measurementDao.deleteMeasurement(measurement)
    }

    suspend fun deleteAll() {
        measurementDao.deleteAll()
    }
}