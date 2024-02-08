package com.mathias8dev.mqttclient.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mathias8dev.mqttclient.domain.viewmodels.SettingsScreenViewModel
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import com.mathias8dev.mqttclient.storage.datastore.model.LocalAppSettings
import com.mathias8dev.mqttclient.ui.composables.ConfirmationDialog
import com.mathias8dev.mqttclient.ui.composables.ContentDetailsLayout
import com.mathias8dev.mqttclient.ui.composables.SuccessDialog
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

    val appSettings = LocalAppSettings.current

    val isSettingsReinitialized by viewModel.settingsReinitializedSuccessfully.collectAsStateWithLifecycle()
    val isDatabaseReinitialized by viewModel.databaseReinitializedSuccessfully.collectAsStateWithLifecycle()

    val showConfirmSettingsReinitializationDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val showConfirmDatabaseReinitializationDialog = rememberSaveable {
        mutableStateOf(false)
    }

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

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    showConfirmSettingsReinitializationDialog.value = true
                }) {
                Text(text = "Réinitialiser les paramètres")
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                onClick = {
                    showConfirmDatabaseReinitializationDialog.value = true
                }) {
                Text(text = "Réinitialiser la base de données")
            }
        }
    }

    if (isDatabaseReinitialized) {
        SuccessDialog(
            message = "La base de données a été réinitialisée avec succès"
        ) {
            viewModel.consumeDatabaseReinitializedEvent()
        }
    }

    if (isSettingsReinitialized) {
        SuccessDialog(
            message = "Les paramètres ont été réinitialisés avec succès"
        ) {
            viewModel.consumeSettingsReinitializedEvent()
        }
    }

    if (showConfirmDatabaseReinitializationDialog.value) {
        ConfirmationDialog(
            message = "Voulez-vous poursuivre votre opération ?",
            onDismissRequest = {
                showConfirmDatabaseReinitializationDialog.value = false
            },
            onConfirmClicked = {
                viewModel.onReinitializeDatabase()
                showConfirmDatabaseReinitializationDialog.value = false
            }
        )
    }

    if (showConfirmSettingsReinitializationDialog.value) {
        ConfirmationDialog(
            message = "Voulez-vous poursuivre votre opération ?",
            onDismissRequest = {
                showConfirmSettingsReinitializationDialog.value = false
            },
            onConfirmClicked = {
                viewModel.onReinitializeSettings()
                showConfirmSettingsReinitializationDialog.value = false
            }
        )
    }
}