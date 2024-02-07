package com.mathias8dev.mqttclient.domain.viewmodels

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    appSettingsStore: DataStore<AppSettings>
): AppSettingsViewModel(appSettingsStore) {



    fun onUseDarkModeChanged(updatedValue: Boolean) {
        viewModelScope.launch {
            appSettingsStore.updateData {
                it.copy(useDarkMode = updatedValue)
            }
        }
    }

    fun onMaintainConnectionToServerActive(updatedValue: Boolean) {
        viewModelScope.launch {
            appSettingsStore.updateData {
                it.copy(maintainConnectionToServerActive = updatedValue)
            }
        }
    }

    fun onUseFloatingMenuChanged(updatedValue: Boolean) {
        viewModelScope.launch {
            appSettingsStore.updateData {
                it.copy(useFloatingMenu = updatedValue)
            }
        }
    }
}