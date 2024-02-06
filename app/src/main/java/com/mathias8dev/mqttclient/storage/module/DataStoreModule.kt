package com.mathias8dev.mqttclient.storage.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import com.mathias8dev.mqttclient.storage.datastore.serializer.AppSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<AppSettings> {
        return MultiProcessDataStoreFactory.create(
            serializer = AppSettingsSerializer(),
            produceFile = {
                File(
                    context.cacheDir,
                    AppSettingsSerializer.filename
                )
            }
        )
    }
}