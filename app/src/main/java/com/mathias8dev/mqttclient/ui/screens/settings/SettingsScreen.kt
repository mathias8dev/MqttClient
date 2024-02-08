package com.mathias8dev.mqttclient.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mathias8dev.mqttclient.domain.viewmodels.SettingsScreenViewModel
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import com.mathias8dev.mqttclient.ui.composables.ContentDetailsLayout
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Composable
@Destination
@RootNavGraph
fun SettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {

    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

    ContentDetailsLayout(
        title = "Paramètres",
        onBackClick = {
            navigator.popBackStack()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsItemSection(
                sectionTitle = "Look and feel"
            ) {
                SwitchSettingItem(
                    actionTitle = "Theme",
                    actionContent = "Utiliser le dark Mode",
                    value = appSettings.useDarkMode,
                    onValueChanged = {
                        viewModel.onAppSettingsChanged(AppSettings::useDarkMode, it)
                    }
                )


                SwitchSettingItem(
                    actionTitle = "Menu",
                    actionContent = "Rendre le menu flottant",
                    value = appSettings.useFloatingMenu,
                    onValueChanged = {
                        viewModel.onAppSettingsChanged(AppSettings::useFloatingMenu, it)
                    }
                )

                SwitchSettingItem(
                    actionTitle = "Zoom",
                    actionContent = "Rendre les graphs zoomables",
                    value = appSettings.isZoomEnabled,
                    onValueChanged = {
                        viewModel.onAppSettingsChanged(AppSettings::isZoomEnabled, it)
                    }
                )

                SwitchSettingItem(
                    actionTitle = "Animation",
                    actionContent = "Animer l'affichage des courbes",
                    value = appSettings.animateChartDisplay,
                    onValueChanged = {
                        viewModel.onAppSettingsChanged(AppSettings::animateChartDisplay, it)
                    }
                )
            }

            SettingsItemSection(
                sectionTitle = "Connexion au serveur"
            ) {
                SwitchSettingItem(
                    actionTitle = "Serveur MQTT",
                    actionContent = "Maintenir la connexion même quand je n'utilise pas l'application",
                    value = appSettings.maintainConnectionToServerActive,
                    onValueChanged = {
                        viewModel.onAppSettingsChanged(
                            AppSettings::maintainConnectionToServerActive,
                            it
                        )
                    }
                )
            }

            SettingsItemSection(
                sectionTitle = "Avancé"
            ) {
                SwitchSettingItem(
                    actionTitle = "Développeur",
                    actionContent = "Activer l'option pour les développeurs",
                    value = appSettings.useDeveloperMode,
                    onValueChanged = {
                        viewModel.onAppSettingsChanged(AppSettings::useDeveloperMode, it)
                    }
                )
            }
        }
    }
}