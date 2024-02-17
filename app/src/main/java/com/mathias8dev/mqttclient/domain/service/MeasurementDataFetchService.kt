package com.mathias8dev.mqttclient.domain.service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mathias8dev.mqttclient.R
import com.mathias8dev.mqttclient.domain.event.EventBus
import com.mathias8dev.mqttclient.domain.event.MQTTClientEvent
import com.mathias8dev.mqttclient.domain.notification.NotificationDataRaw
import com.mathias8dev.mqttclient.domain.notification.NotificationUtils
import com.mathias8dev.mqttclient.domain.notification.toNotificationBuilder
import com.mathias8dev.mqttclient.domain.receiver.FetcherBroadcastReceiver
import com.mathias8dev.mqttclient.domain.utils.toMQTTServerUri
import com.mathias8dev.mqttclient.storage.datastore.model.AppSettings
import com.mathias8dev.mqttclient.storage.dto.MeasurementDto
import com.mathias8dev.mqttclient.storage.dto.toMeasurement
import com.mathias8dev.mqttclient.storage.room.model.Config
import com.mathias8dev.mqttclient.storage.room.repository.ConfigRepository
import com.mathias8dev.mqttclient.storage.room.repository.MeasurementRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MeasurementDataFetchService : LifecycleService() {

    @Inject
    lateinit var appSettingsStore: DataStore<AppSettings>

    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var measurementRepository: MeasurementRepository

    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var wakeLockJob: Job

    private var isJobRunning: Boolean = false
    private val mqttOptions = MqttConnectOptions()
    private var mqttClient: MqttAndroidClient? = null
    private var latestConfig: Config? = null


    @RequiresApi(Build.VERSION_CODES.N)
    private fun onInit() {
        Timber.d("DataFetchService: Service init")
        Timber.d("DataFetchService: Dependencies initialized ? ${::measurementRepository.isInitialized}, ${::configRepository.isInitialized}")
        mqttOptions.isAutomaticReconnect = true
        mqttOptions.isCleanSession = true
        mqttOptions.connectionTimeout = 10

        Timber.d("DataFetchService: Initialize the notification channel")
        NotificationUtils.initDefaultChannel(this)
        Timber.d("DataFetchService: Notification channel initialized")


        lifecycleScope.launch {
            appSettingsStore.data.collectLatest {
                configRepository.findConfigById(it.selectedConfigId).let { config ->
                    Timber.d("DataFetchService: The found config with id ${it.selectedConfigId} is $config")
                    latestConfig = config?.copy()
                    onConfigChanged()
                }
            }
        }
    }

    private fun onConfigChanged() {

        latestConfig?.let { config ->
            Timber.d("DataFetchService: OnConfigChanged, the config is $config")
            runCatching {
                mqttClient = MqttAndroidClient(
                    this,
                    config.toMQTTServerUri(),
                    MqttClient.generateClientId(),
                )
            }.onFailure {
                lifecycleScope.launch {
                    Timber.d("DataFetchService: Exception, the config is $config")
                    EventBus.publish(
                        MQTTClientEvent.MQTTClientInitializationException(
                            config = config.copy(),
                            cause = it
                        )
                    )
                }
            }

            if (isJobRunning) {
                stopJob()
                doJob()
            }
        } ?: stopJob() // Stop the job if null
    }

    private fun publishIdle() {
        lifecycleScope.launch {
            EventBus.publish(MQTTClientEvent.IDLE)
        }
    }


    private fun fetchData() {
        Timber.d("DataFetchService: FetchData called")
        if (mqttClient !== null && latestConfig != null) {
            Timber.d("DataFetchService: Client initialized and config not null")
            runCatching {
                val token =mqttClient?.connect(mqttOptions)
                token?.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken)                        {
                        Timber.i("Connectied successfully")
                        Log.d("MeasurementDataFetch", "client connected successfully")
                        onConnectedSuccessfully()
                    }
                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        //connectionStatus = false
                        Timber.i("Impossible to connect")
                        lifecycleScope.launch {
                            EventBus.publish(
                                MQTTClientEvent.MQTTClientConnectException(
                                    config = latestConfig?.copy(),
                                    cause = exception
                                )
                            )
                        }
                    }
                }
            }.onFailure {
                Timber.d("DataFetchService: An error occurred when trying to connect to the server")
                lifecycleScope.launch {
                    EventBus.publish(
                        MQTTClientEvent.MQTTClientConnectException(
                            config = latestConfig?.copy(),
                            cause = it
                        )
                    )
                }
            }.onSuccess {
                lifecycleScope.launch {
                    EventBus.publish(
                        MQTTClientEvent.MQTTClientConnectedToServer
                    )
                }


            }
        }
    }

    private fun onConnectedSuccessfully() {
        runCatching {
            mqttClient?.let {
                it.subscribe(
                    latestConfig!!.currentTopic,
                    2,
                    null,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            listenToMessages()
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            Timber.d("DataFetchService: An error occurred when subscribing to the topic")
                            lifecycleScope.launch {
                                EventBus.publish(
                                    MQTTClientEvent.MQTTClientTopicSubscriptionException(
                                        config = latestConfig?.copy(),
                                        cause = exception ?: Exception()
                                    )
                                )
                            }
                        }
                    })
            }
        }.onFailure {
            Timber.d("DataFetchService: An error occurred when subscribing to the topic")
            lifecycleScope.launch {
                EventBus.publish(
                    MQTTClientEvent.MQTTClientTopicSubscriptionException(
                        config = latestConfig?.copy(),
                        cause = it
                    )
                )
            }
        }

    }

    private fun listenToMessages() {
        mqttClient?.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                //connectionStatus = false
                // Give your callback on failure here
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                try {
                    Log.d("MeasurementDataFetch", "Message received")
                    val data = String(message.payload, charset("UTF-8"))
                    val gson = Gson()
                    val measurementDto =
                        gson.fromJson(data, MeasurementDto::class.java)
                    Timber.d("DataFetchService: The retrieved payload is $measurementDto")
                    lifecycleScope.launch {
                        measurementRepository.insertMeasurement(
                            measurementDto.toMeasurement()
                        )
                    }

                } catch (e: Exception) {
                    Timber.d("An error occurred when receiving message")
                    Timber.e(e)
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Acknowledgement on delivery complete
            }
        })
    }

    private fun doJob() {
        Timber.d("DataFetchService: doJob called")
        isJobRunning = true
        fetchData()
    }

    private fun stopJob() {
        if (!isJobRunning || mqttClient == null) return

        isJobRunning = false
        runCatching {
            mqttClient?.disconnect()
        }.onFailure {
            Timber.d("DataFetchService: An error occurred when disconnecting from the server")
            lifecycleScope.launch {
                EventBus.publish(
                    MQTTClientEvent.MQTTClientDisconnectException(
                        config = latestConfig,
                        cause = it
                    )
                )
            }
        }.onSuccess {
            lifecycleScope.launch {
                EventBus.publish(
                    MQTTClientEvent.MQTTClientDisconnectedFromServer
                )
            }
        }
    }

    private fun startWakeLock() {
        Timber.d("DataFetchService: Acquiring the wakelock")
        val frequency = 120 * 60 * 1000L /*120 minutes*/
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            MeasurementDataFetchService::class.java.simpleName
        )
        wakeLockJob = startRepeatingWakelockJob(frequency)
    }


    private fun startRepeatingWakelockJob(timeInterval: Long): Job {
        return lifecycleScope.launch(Dispatchers.Default) {
            while (isActive) {
                try {
                    wakeLock?.release()
                } catch (e: Exception) {
                    Timber.i("DataFetchService: ignore: cannot release the wakelock")
                }
                Timber.i("DataFetchService: Acquiring the wakelock again")
                // let's keep 1 second without a wakelock
                wakeLock?.acquire(timeInterval - 1000L)
                delay(timeInterval)
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        currentService = this
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("DataFetchService: Service onStart called")
        startWakeLock()
        Timber.d("DataFetchService: WakeLock acquired")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                Timber.i("DataFetchService: starting foreground process")
                val notification = NotificationDataRaw(
                    notificationId,
                    iconRes = R.drawable.ic_launcher_background,
                    content = "Le service de récupération des données est lancé",
                    title = "MQTTClient"
                ).toNotificationBuilder(this, NotificationUtils.Channel.Default.channelId)
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        notificationId,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                    )
                } else {
                    startForeground(
                        notificationId,
                        notification
                    )
                }
                Timber.i("DataFetchService: Starting foreground process successful!")
            }.onFailure {
                Timber.e("DataFetchService: Error starting foreground process ${it.message}")
            }
        }
        onInit()
        doJob()
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("DataFetchService: onDestroy: Service is destroyed :( ")
        stopJob()
        Intent(this, FetcherBroadcastReceiver::class.java).let {
            sendBroadcast(it)
        }

    }


    companion object {
        var currentService: MeasurementDataFetchService? = null
        private const val notificationId = 974
    }


}