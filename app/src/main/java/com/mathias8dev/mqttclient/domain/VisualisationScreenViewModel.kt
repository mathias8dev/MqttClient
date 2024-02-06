package com.mathias8dev.mqttclient.domain

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import com.mathias8dev.mqttclient.storage.room.model.Measurement
import com.mathias8dev.mqttclient.storage.room.repository.ConfigRepository
import com.mathias8dev.mqttclient.storage.room.repository.MeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VisualisationScreenViewModel @Inject constructor(
    private val appSettingsStore: DataStore<AppSettings>,
    private val configurationRepository: ConfigRepository,
    private val measurementRepository: MeasurementRepository
): ViewModel() {

    private val _measurements = MutableStateFlow<List<Measurement>>(emptyList())
    val measurements = _measurements.asStateFlow()

    private val _hasDefinedConfig = MutableStateFlow(true)
    val hasDefinedConfig = _hasDefinedConfig.asStateFlow()

    init {

        viewModelScope.launch {
            measurementRepository.getAllMeasurements().collect{
                _measurements.emit(it)
            }

            appSettingsStore.data.collect {
                _hasDefinedConfig.emit(it.selectedConfigId == -1L)
            }
        }
    }

}