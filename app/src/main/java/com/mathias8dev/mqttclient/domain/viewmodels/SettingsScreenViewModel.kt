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
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1


@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    appSettingsStore: DataStore<AppSettings>
): AppSettingsViewModel(appSettingsStore)