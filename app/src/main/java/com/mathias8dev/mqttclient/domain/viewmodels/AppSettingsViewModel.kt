package com.mathias8dev.mqttclient.domain.viewmodels

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KProperty1

abstract class AppSettingsViewModel(
    protected val appSettingsStore: DataStore<AppSettings>
): ViewModel() {

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings = _appSettings.asStateFlow()

    init {
        viewModelScope.launch {
            appSettingsStore.data.collectLatest {
                _appSettings.emit(it)
            }
        }
    }

    fun <V> onAppSettingsChanged(property: KProperty1<AppSettings, V>, updatedValue: V) {
        viewModelScope.launch {
            appSettingsStore.updateData {
                val updated = it.copy()
                updated.javaClass.declaredFields.find { field -> field.name == property.name }?.let {field ->
                    field.isAccessible = true
                    field.set(updated, updatedValue)
                }

                updated
            }
        }
    }
}