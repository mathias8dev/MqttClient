package com.mathias8dev.mqttclient.domain.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.mathias8dev.mqttclient.domain.event.EventBus
import com.mathias8dev.mqttclient.domain.event.LoggingEvent
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    appSettingsStore: DataStore<AppSettings>
): AppSettingsViewModel(appSettingsStore) {

    private val _loggingEvents = mutableStateListOf<LoggingEvent>()
    val loggingEvents: List<LoggingEvent>
        get() = _loggingEvents

    init {
        viewModelScope.launch {
            EventBus.subscribe<LoggingEvent> {
                _loggingEvents.add(it)
            }
        }
    }
}