package com.mathias8dev.mqttclient.domain.viewmodels

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import com.mathias8dev.mqttclient.storage.room.repository.ConfigRepository
import com.mathias8dev.mqttclient.storage.room.repository.MeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    appSettingsStore: DataStore<AppSettings>,
    private val configRepository: ConfigRepository,
    private val measurementRepository: MeasurementRepository
): AppSettingsViewModel(appSettingsStore) {

    private val _settingsReinitializedSuccessfully = MutableStateFlow(false)
    val settingsReinitializedSuccessfully = _settingsReinitializedSuccessfully.asStateFlow()

    private val _databaseReinitializedSuccessfully = MutableStateFlow(false)
    val databaseReinitializedSuccessfully = _databaseReinitializedSuccessfully.asStateFlow()


    fun onReinitializeDatabase() {
        viewModelScope.launch {
            configRepository.deleteAll()
            measurementRepository.deleteAll()
            _databaseReinitializedSuccessfully.emit(true)
        }

    }

    fun onReinitializeSettings() {
        viewModelScope.launch {
            appSettingsStore.updateData {
                AppSettings()
            }
            _settingsReinitializedSuccessfully.emit(true)
        }
    }

    fun consumeSettingsReinitializedEvent() {
        viewModelScope.launch {
            _settingsReinitializedSuccessfully.emit(false)
        }
    }

    fun consumeDatabaseReinitializedEvent() {
        viewModelScope.launch {
            _databaseReinitializedSuccessfully.emit(false)
        }
    }
}