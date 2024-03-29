package com.mathias8dev.mqttclient.storage.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mathias8dev.mqttclient.storage.room.model.Config
import kotlinx.coroutines.flow.Flow


@Dao
interface ConfigDao {

    @Query("SELECT * FROM configs")
    suspend fun getAllConfigs(): List<Config>

    @Query("SELECT * FROM configs")
    fun getAllConfigsFlow(): Flow<List<Config>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: Config)

    @Query("SELECT * FROM configs ORDER BY id DESC LIMIT 1")
    fun getLatestConfigFlow(): Flow<Config?>

    @Delete
    suspend fun deleteConfig(config: Config)

    @Query("SELECT * FROM configs WHERE id = :configId")
    suspend fun findConfigById(configId: Long): Config?

    @Query("DELETE FROM configs")
    suspend fun deleteAll()
}