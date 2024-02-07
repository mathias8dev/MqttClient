package com.mathias8dev.mqttclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.mathias8dev.mqttclient.ui.theme.MqttClientTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)



        setContent {

            val viewModel: MainActivityViewModel = hiltViewModel()
            val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

            MqttClientTheme(
                darkTheme = appSettings.useDarkMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    TransparentStatusBar()

                    Box(modifier = Modifier.fillMaxSize()) {
                        EventListener {showContent, content ->
                            if (showContent && content != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(MaterialTheme.colorScheme.primary),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null
                                    )

                                    Text(text = content)
                                }
                            }
                        }

                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .navigationBarsPadding()
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
fun EventListener(
    content: @Composable (showContent: Boolean, content: String?)->Unit
) {

    val showNotification = rememberSaveable {
        mutableStateOf(false)
    }

    val showNotificationMessage = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        EventBus.subscribe<MQTTClientEvent> {
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
                    showNotification.value = true
                    showNotificationMessage.value =
                        "Une erreur est survenue en initialisant le client, en se connectant au serveur ou en soubscrivant au topic ou en se déconnectant"
                    Timber.e(it.cause)
                }

                is MQTTClientEvent.IDle -> {
                    showNotification.value = false
                    showNotificationMessage.value = null
                }
            }
        }
    }

    content(
        showNotification.value,
        showNotificationMessage.value
    )
}