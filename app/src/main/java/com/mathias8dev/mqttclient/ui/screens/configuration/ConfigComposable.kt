package com.mathias8dev.mqttclient.ui.screens.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mathias8dev.mqttclient.R
import com.mathias8dev.mqttclient.storage.room.model.Config


@Composable
fun ConfigComposable(
    currentSelectedConfigId: Long = -1,
    config: Config,
    onConfigToggle: (selectedConfig: Config)->Unit,
    onRemoveConfig: (configToRemove: Config)->Unit
) {

    val textColor = if (currentSelectedConfigId == config.id)
        MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSecondary

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (currentSelectedConfigId == config.id)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConfigItem(
                    drawableRes = R.drawable.ic_settings_remote_24,
                    value = config.serverUrl,
                    title = "URL du serveur",
                    textColor = textColor
                )
                ConfigItem(
                    drawableRes = R.drawable.ic_numbers_24,
                    value = config.serverPort,
                    title = "Port du serveur",
                            textColor = textColor
                )

                ConfigItem(
                    drawableRes = R.drawable.ic_text_fields_24,
                    value = config.currentTopic,
                    title = "Topic auquel se connecter",
                    textColor = textColor
                )
            }
            Spacer(
                modifier = Modifier
                    .width(16.dp)
                    .weight(1f)
            )

            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    onRemoveConfig(config)
                }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }

            Switch(
                modifier = Modifier.scale(0.8f),
                checked = currentSelectedConfigId == config.id,
                onCheckedChange = { onConfigToggle(config) }
            )

        }

    }
}