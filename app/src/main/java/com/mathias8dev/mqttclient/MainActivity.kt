package com.mathias8dev.mqttclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.mathias8dev.mqttclient.domain.event.EventBus
import com.mathias8dev.mqttclient.domain.event.MQTTClientEvent
import com.mathias8dev.mqttclient.domain.viewmodels.MainActivityViewModel
import com.mathias8dev.mqttclient.ui.composables.TransparentStatusBar
import com.mathias8dev.mqttclient.ui.screens.NavGraphs
import com.mathias8dev.mqttclient.ui.screens.destinations.VisualisationScreenDestination
import com.mathias8dev.mqttclient.ui.theme.MqttClientTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.utils.currentDestinationFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)



        setContent {

            val viewModel: MainActivityViewModel = hiltViewModel()
            val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            val currentDestinationIsVisualisationScreen by produceState(
                initialValue = true,
                producer = {
                    navController.currentDestinationFlow.collectLatest {
                        value = it is VisualisationScreenDestination
                    }
                }
            )

            MqttClientTheme(
                darkTheme = appSettings.useDarkMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                    ) {
                        EventListener { isException, showContent, content ->

                            if (showContent &&
                                content != null &&
                                currentDestinationIsVisualisationScreen
                            ) {
                                EventDisplay(
                                    isException,
                                    content
                                )
                            }
                        }
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                        ) { _ ->
                            DestinationsNavHost(
                                navController = navController,
                                navGraph = NavGraphs.root
                            )
                        }

                    }
                }
            }
        }
    }

}

@Composable
fun EventDisplay(
    isException: Boolean,
    content: String
) {

    val show by produceState(initialValue = true, producer = {
        value = if (!isException) {
            delay(5.seconds)
            false
        } else true
    })

    AnimatedVisibility(
        visible = show
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    if (isException) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                ),
        ) {

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isException) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSecondaryContainer
                )

                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun EventListener(
    content: @Composable (
        isException: Boolean,
        showContent: Boolean,
        content: String?
    ) -> Unit
) {

    val showNotification = rememberSaveable {
        mutableStateOf(false)
    }

    val showNotificationMessage = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val isException = rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        EventBus.subscribe<MQTTClientEvent> {
            Timber.d("EventBus, mqtt event received: ", it)
            isException.value = it is MQTTClientEvent.MQTTClientException
            when (it) {
                is MQTTClientEvent.MQTTClientDisconnectedFromServer -> {
                    showNotification.value = true
                    showNotificationMessage.value = "La connexion avec le serveur MQTT a été rompue"
                }

                is MQTTClientEvent.MQTTClientConnectedToServer -> {
                    showNotification.value = true
                    showNotificationMessage.value =
                        "La connexion avec le serveur a été établie avec succès"
                }

                is MQTTClientEvent.MQTTClientException -> {
                    Timber.d("The used config is : ${it.config}")
                    Timber.e("The cause is ${it.cause}")
                    when (it) {
                        is MQTTClientEvent.MQTTClientDisconnectException -> {
                            showNotification.value = true
                            showNotificationMessage.value =
                                "Une erreur est survenue en se déconnectant du serveur"
                        }

                        is MQTTClientEvent.MQTTClientConnectException -> {
                            showNotification.value = true
                            showNotificationMessage.value =
                                "Une erreur est survenue en se connectant au serveur"
                        }

                        is MQTTClientEvent.MQTTClientInitializationException -> {
                            showNotification.value = true
                            showNotificationMessage.value =
                                "Une erreur est survenue en initializant le client mqtt"
                        }

                        is MQTTClientEvent.MQTTClientTopicSubscriptionException -> {
                            showNotification.value = true
                            showNotificationMessage.value =
                                "Une erreur est survenue en essayant de subscribe au topic"
                        }

                        else -> {

                        }
                    }
                }

                is MQTTClientEvent.IDLE -> {
                    showNotification.value = false
                    showNotificationMessage.value = null
                }

                else -> {}
            }
        }
    }

    content(
        isException.value,
        showNotification.value,
        showNotificationMessage.value
    )
}