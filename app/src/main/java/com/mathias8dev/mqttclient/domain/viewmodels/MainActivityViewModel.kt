package com.mathias8dev.mqttclient.domain.viewmodels

import androidx.datastore.core.DataStore
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    appSettingsStore: DataStore<AppSettings>
): AppSettingsViewModel(appSettingsStore)