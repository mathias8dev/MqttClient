package com.mathias8dev.mqttclient.domain.service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.PowerManager
import androidx.datastore.core.DataStore
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mathias8dev.mqttclient.domain.event.EventBus
import com.mathias8dev.mqttclient.domain.event.MQTTClientEvent
import com.mathias8dev.mqttclient.domain.notification.ServiceNotification
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
import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
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
    private var currentServiceNotification: ServiceNotification? = null
    private lateinit var wakeLockJob: Job

    private var isJobRunning: Boolean = false
    private val mqttOptions = MqttConnectOptions()
    private lateinit var mqttClient: IMqttClient
    private var latestConfig: Config? = null

    private fun onInit() {
        Timber.d("Service init")
        Timber.d("Dependencies initialized ? ${::measurementRepository.isInitialized}, ${::configRepository.isInitialized}")
        mqttOptions.isAutomaticReconnect = true
        mqttOptions.isCleanSession = true
        mqttOptions.connectionTimeout = 10


        lifecycleScope.launch {
            appSettingsStore.data.collectLatest {
                configRepository.findConfigById(it.selectedConfigId).let { config ->
                    Timber.d("The found config with id ${it.selectedConfigId} is $config")
                    latestConfig = config?.copy()
                    onConfigChanged()
                }
            }
        }
    }

    private fun onConfigChanged() {

        latestConfig?.let {config ->
            Timber.d("OnConfigChanged, the config is $config")
            runCatching {
                mqttClient = MqttClient(
                    config.toMQTTServerUri(),
                    MqttClient.generateClientId()
                )
            }.onFailure {
                lifecycleScope.launch {
                    Timber.d("Exception, the config is $config")
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
        Timber.d("FetchData called")
        if (::mqttClient.isInitialized && latestConfig != null) {
            Timber.d("Client initialized and config not null")
            runCatching {
                mqttClient.connect(mqttOptions)
            }.onFailure {
                Timber.d("An error occurred when trying to connect to the server")
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
                runCatching {
                    mqttClient.subscribe(latestConfig!!.currentTopic) { _, msg ->
                        val payload: ByteArray = msg.payload

                        val gson = Gson()
                        val measurementDto = gson.fromJson(String(payload), MeasurementDto::class.java)
                        Timber.d("The retrieved payload is $measurementDto")
                        lifecycleScope.launch {
                            measurementRepository.insertMeasurement(
                                measurementDto.toMeasurement()
                            )
                        }

                    }
                }.onFailure {
                    Timber.d("An error occurred when subscribing to the topic")
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

        }

    }

    private fun doJob() {
        Timber.d("doJob called")
        isJobRunning = true
        fetchData()
    }

    private fun stopJob() {
        if (!isJobRunning || !::mqttClient.isInitialized) return

        isJobRunning = false
        runCatching {
            mqttClient.disconnect()
        }.onFailure {
            Timber.d("An error occurred when disconnecting from the server")
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
        Timber.d("Acquiring the wakelock")
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
                    Timber.i("ignore: cannot release the wakelock")
                }
                Timber.i("Acquiring the wakelock again")
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


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("Service onStart called")
        startWakeLock()
        Timber.d("WakeLock acquired")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                Timber.i("starting foreground process")
                currentServiceNotification = ServiceNotification(this, NOTIFICATION_ID, false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        NOTIFICATION_ID,
                        currentServiceNotification!!.notification!!,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                    )
                } else {
                    startForeground(NOTIFICATION_ID, currentServiceNotification!!.notification)
                }
                Timber.i("Starting foreground process successful!")
            }.onFailure {
                Timber.e("Error starting foreground process ${it.message}")
            }
        }
        onInit()
        doJob()
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy: Service is destroyed :( ")
        stopJob()
        Intent(this, FetcherBroadcastReceiver::class.java).let {
            sendBroadcast(it)
        }

    }


    companion object {
        var currentService: MeasurementDataFetchService? = null

        private const val NOTIFICATION_ID = 9974

    }


}