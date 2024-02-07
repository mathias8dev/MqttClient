package com.mathias8dev.mqttclient.ui.screens.visualisation

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mathias8dev.mqttclient.domain.viewmodels.VisualisationScreenViewModel
import com.mathias8dev.mqttclient.ui.composables.ErrorDialog
import com.mathias8dev.mqttclient.ui.screens.destinations.ConfigurationScreenDestination
import com.mathias8dev.mqttclient.ui.screens.destinations.SettingsScreenDestination
import com.mathias8dev.mqttclient.ui.utils.useModifierIf
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
        mutableStateOf(false)
    }

    val useFloatingMenu by viewModel.useFloatingMenu.collectAsStateWithLifecycle()

    val measurements = viewModel.measurements.collectAsStateWithLifecycle()
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { _, offsetChange, _->
        offset += offsetChange
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.hasDefinedConfig.collectLatest { hasDefinedConfig ->

            showNoConfigurationDefinedDialog.value = !hasDefinedConfig
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "Historique des données reçues")
            Spacer(modifier = Modifier.height(16.dp))
            measurements.value.forEach {
                MeasurementItem(
                    name = "Temperature",
                    value = it.temperature.toString()
                )
                Spacer(modifier = Modifier.height(10.dp))
                MeasurementItem(
                    name = "Humidité",
                    value = it.humidity.toString()
                )
                Spacer(modifier = Modifier.height(10.dp))
                MeasurementItem(
                    name = "CO2",
                    value = it.co2.toString()
                )
                Spacer(modifier = Modifier.height(10.dp))
                MeasurementItem(
                    name = "PH",
                    value = it.ph.toString()
                )
                Spacer(modifier = Modifier.height(10.dp))
                MeasurementItem(
                    name = "Alcool",
                    value = it.alcohol.toString()
                )
                Spacer(modifier = Modifier.height(10.dp))

            }
        }

        FloatingMenu(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 48.dp)
                .useModifierIf(useFloatingMenu) {
                    it
                        .graphicsLayer {
                            translationX = offset.x
                            translationY = offset.y
                        }
                        .transformable(state)
                },
            onSettingsClicked = {
                navigator.navigate(SettingsScreenDestination)
            },
            onConfigsClicked = {
                navigator.navigate(ConfigurationScreenDestination)
            }
        )
    }

    if (showNoConfigurationDefinedDialog.value) {
        ErrorDialog(
            errorMessage = "Aucune configuration n'a été définie",
            onDismissRequest = {
                navigator.navigate(ConfigurationScreenDestination)
            }
        )
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