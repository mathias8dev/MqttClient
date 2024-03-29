package com.mathias8dev.mqttclient.ui.screens.visualisation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mathias8dev.mqttclient.domain.viewmodels.VisualisationScreenViewModel
import com.mathias8dev.mqttclient.storage.datastore.model.LocalAppSettings
import com.mathias8dev.mqttclient.storage.room.model.toChartModels
import com.mathias8dev.mqttclient.ui.composables.ErrorDialog
import com.mathias8dev.mqttclient.ui.screens.destinations.ConfigurationScreenDestination
import com.mathias8dev.mqttclient.ui.screens.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest


@Composable
@Destination
@RootNavGraph(start = true)
fun VisualisationScreen(
    navigator: DestinationsNavigator,
    viewModel: VisualisationScreenViewModel = hiltViewModel()
) {

    val showNoConfigurationDefinedDialog = rememberSaveable {
        mutableStateOf(true)
    }
    val showDevPanel = rememberSaveable {
        mutableStateOf(false)
    }

    val useFloatingMenu = LocalAppSettings.current.useFloatingMenu
    val isZoomEnabled =  LocalAppSettings.current.isZoomEnabled
    val animateChartDisplay =  LocalAppSettings.current.animateChartDisplay
    val useDeveloperMode =  LocalAppSettings.current.useDeveloperMode

    val measurements = viewModel.measurements.collectAsStateWithLifecycle()



    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            measurements.value.toChartModels().let {
                MeasurementDisplay(
                    modifier = Modifier.padding(top = 16.dp),
                    chartTitle = "Temperature",
                    chartModel = it.temperature,
                    chartHeight = 200.dp,
                    isZoomEnabled = isZoomEnabled,
                    animateChartDisplay = animateChartDisplay
                )

                MeasurementDisplay(
                    modifier = Modifier.padding(top = 16.dp),
                    chartTitle = "Humidité",
                    chartModel = it.humidity,
                    chartHeight = 200.dp,
                    isZoomEnabled = isZoomEnabled,
                    animateChartDisplay = animateChartDisplay
                )

                MeasurementDisplay(
                    modifier = Modifier.padding(top = 16.dp),
                    chartTitle = "CO2",
                    chartModel = it.co2,
                    chartHeight = 200.dp,
                    isZoomEnabled = isZoomEnabled,
                    animateChartDisplay = animateChartDisplay
                )

                MeasurementDisplay(
                    modifier = Modifier.padding(top = 16.dp),
                    chartTitle = "PH",
                    chartModel = it.ph,
                    chartHeight = 200.dp,
                    isZoomEnabled = isZoomEnabled,
                    animateChartDisplay = animateChartDisplay
                )

                MeasurementDisplay(
                    modifier = Modifier.padding(top = 16.dp),
                    chartTitle = "Alcool",
                    chartModel = it.alcohol,
                    chartHeight = 200.dp,
                    isZoomEnabled = isZoomEnabled,
                    animateChartDisplay = animateChartDisplay
                )
            }
        }

        FloatingMenu(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 48.dp),
            makeMenuMovable = useFloatingMenu,
            showDevPanelMenu = useDeveloperMode,
            onSettingsClicked = {
                navigator.navigate(SettingsScreenDestination)
            },
            onConfigsClicked = {
                navigator.navigate(ConfigurationScreenDestination)
            },
            onDevPanelClicked = {
                showDevPanel.value = !showDevPanel.value
            }
        )
    }

    if (showNoConfigurationDefinedDialog.value && LocalAppSettings.current.selectedConfigId == -1L) {
        ErrorDialog(
            errorMessage = "Aucune configuration n'a été définie",
            onDismissRequest = {
                showNoConfigurationDefinedDialog.value = false
            },
            onOkayClicked = {
                navigator.navigate(ConfigurationScreenDestination)
            }
        )
    }

    FloatingDevPanel(open = showDevPanel.value) {
        showDevPanel.value = false
    }
}

@Composable
fun MeasurementItem(
    name: String,
    value: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$name: ", modifier = Modifier.fillMaxWidth(0.4f))
        Text(text = value)
    }

}