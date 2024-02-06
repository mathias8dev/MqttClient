package com.mathias8dev.mqttclient.storage.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mathias8dev.mqttclient.storage.room.dao.ConfigDao
import com.mathias8dev.mqttclient.storage.room.dao.MeasurementDao
import com.mathias8dev.mqttclient.storage.room.model.Config
import com.mathias8dev.mqttclient.storage.room.model.Measurement



@Database(entities = [Measurement::class, Config::class], version = 1)
abstract class MQTTDatabase : RoomDatabase() {

    abstract fun measurementDao(): MeasurementDao

    abstract fun configDao(): ConfigDao


    companion object {
        const val DB_NAME = "mqtt_database"
    }
}