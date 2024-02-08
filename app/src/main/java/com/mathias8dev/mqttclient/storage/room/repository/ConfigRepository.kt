package com.mathias8dev.mqttclient.storage.room.repository

import com.mathias8dev.mqttclient.storage.room.dao.ConfigDao
import com.mathias8dev.mqttclient.storage.room.model.Config
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


class ConfigRepository @Inject constructor(
    private val configDao: ConfigDao
) {

    suspend fun insertConfig(config: Config) {
        configDao.insertConfig(config)
    }

    suspend fun deleteConfig(config: Config) {
        configDao.deleteConfig(config)
    }

    suspend fun getLatestConfig(): Flow<Config?> {
        return channelFlow {
            configDao.getAllConfigsFlow().collectLatest { configs ->
                if (configs.isNotEmpty()) send(configs.last())
                else send(null)

            }

        }
    }

    suspend fun getAllConfigs(): List<Config> {
        return configDao.getAllConfigs()
    }

    fun getAllConfigsFlow(): Flow<List<Config>> {
        return configDao.getAllConfigsFlow()
    }

    fun fastLatestConfig(): Flow<Config?> {
        return configDao.getLatestConfigFlow()
    }

    suspend fun findConfigById(configId: Long): Config? {
        return configDao.findConfigById(configId)
    }

}