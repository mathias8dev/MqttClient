package com.mathias8dev.mqttclient.ui.screens.visualisation

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.mathias8dev.mqttclient.ui.composables.StandardDialog


@Composable
fun FloatingDevPanel(
    open: Boolean = false,
    onDismiss: () -> Unit,
) {

    if (open) {

        StandardDialog(
            onDismissRequest = onDismiss
        ) {
            Text(
                text = "Dev panel",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}