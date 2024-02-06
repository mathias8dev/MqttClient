package com.mathias8dev.mqttclient.storage.module

import android.content.Context
import androidx.room.Room
import com.mathias8dev.mqttclient.storage.room.MQTTDatabase
import com.mathias8dev.mqttclient.storage.room.dao.ConfigDao
import com.mathias8dev.mqttclient.storage.room.dao.MeasurementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class StorageModule {


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): MQTTDatabase {
        return Room.databaseBuilder(
            context,
            MQTTDatabase::class.java,
            MQTTDatabase.DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideConfigDao(database: MQTTDatabase): ConfigDao {
        return database.configDao()
    }


    @Provides
    @Singleton
    fun provideMeasurementDao(database: MQTTDatabase): MeasurementDao {
        return database.measurementDao()
    }
}