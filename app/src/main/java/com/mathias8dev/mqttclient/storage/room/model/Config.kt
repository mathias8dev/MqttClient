package com.mathias8dev.mqttclient.storage.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.mathias8dev.yup.Yup


@Entity(tableName = "configs")
data class Config(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("server_url")
    val serverUrl: String,
    @ColumnInfo("server_port")
    val serverPort: String,
    @ColumnInfo("current_topic")
    val currentTopic: String
): Yup.Record