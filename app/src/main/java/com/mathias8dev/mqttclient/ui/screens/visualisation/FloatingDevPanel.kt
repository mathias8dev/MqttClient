package com.mathias8dev.mqttclient.ui.screens.visualisation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mathias8dev.mqttclient.R
import com.mathias8dev.mqttclient.domain.event.LocalLoggingEvents
import com.mathias8dev.mqttclient.ui.composables.LottieAnimation
import com.mathias8dev.mqttclient.ui.composables.StandardDialog


@Composable
fun FloatingDevPanel(
    open: Boolean = false,
    onDismiss: () -> Unit,
) {

    if (open) {
        val logs = LocalLoggingEvents.current
        StandardDialog(
            onDismissRequest = onDismiss
        ) {
            Text(
                text = "Dev panel",
                style = MaterialTheme.typography.titleSmall
            )

            if (logs.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.dp)
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = rememberLazyListState()
                ) {
                    itemsIndexed(
                        items = logs,
                        key = { _, log -> log.hashCode() }) { _, log ->

                        Text(
                            text = "${if (log.tag != null) log.tag + ": " else ""} ${log.message}",
                            style =MaterialTheme.typography.bodySmall
                        )

                        log.throwable?.let {
                            Text(
                                text = it.stackTraceToString(),
                                style =MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieAnimation(
                            modifier = Modifier.size(92.dp),
                            animationRes = R.raw.lottie_empty
                        )
                        Text(
                            text = "Aucun log à afficher",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}