package com.mathias8dev.mqttclient.domain.viewmodels

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import com.mathias8dev.mqttclient.storage.room.model.Config
import com.mathias8dev.mqttclient.storage.room.repository.ConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ConfigurationScreenViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val appSettingsDataStore: DataStore<AppSettings>,
) : ViewModel() {

    private val _configurationsFlow = MutableStateFlow<List<Config>>(emptyList())
    val configurationsFlow = _configurationsFlow.asStateFlow()

    private val _currentSelectedConfigId = MutableStateFlow(-1L)
    val currentSelectedConfigId = _currentSelectedConfigId.asStateFlow()

    private val _showConfigRemovedDialog = MutableStateFlow(false)
    val showConfigRemovedDialog = _showConfigRemovedDialog.asStateFlow()
    private val _showConfigInsertedDialog = MutableStateFlow(false)
    val showConfigInsertedDialog = _showConfigInsertedDialog.asStateFlow()

    init {
        Log.d(ConfigurationScreenViewModel::class.java.simpleName, "The list of configs is ${configurationsFlow.value}")
        viewModelScope.launch {
            launch {
                configRepository.getAllConfigsFlow().collect {
                    Log.d(ConfigurationScreenViewModel::class.java.simpleName, "The list of configs is $it")
                    _configurationsFlow.emit(it)
                }
            }

            launch {
                appSettingsDataStore.data.collect {
                    _currentSelectedConfigId.emit(
                        it.selectedConfigId
                    )
                }
            }
        }
    }


    fun insertConfig(config: Config) {
        viewModelScope.launch(Dispatchers.IO) {
            configRepository.insertConfig(config)
            _showConfigInsertedDialog.emit(true)
        }
    }

    fun onRemoveConfig(config: Config) {
        viewModelScope.launch {
            configRepository.deleteConfig(
                config
            )
            appSettingsDataStore.updateData {
                it.copy(selectedConfigId = -1)
            }

            _showConfigRemovedDialog.emit(true)
        }
    }

    fun onToggleConfig(config: Config) {
        viewModelScope.launch {

            val updatedId = if (currentSelectedConfigId.value == config.id) -1
            else config.id
            appSettingsDataStore.updateData {
                it.copy(selectedConfigId = updatedId)
            }

        }
    }

    fun consumeShowConfigRemovedDialogEvent() {
        viewModelScope.launch {
            _showConfigRemovedDialog.emit(false)
        }
    }

    fun consumeShowConfigInsertedDialogEvent() {
        viewModelScope.launch {
            _showConfigInsertedDialog.emit(false)
        }
    }
}