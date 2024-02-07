package com.mathias8dev.mqttclient.ui.screens.visualisation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mathias8dev.mqttclient.R


@Composable
fun FloatingMenu(
    modifier: Modifier = Modifier,
    onConfigsClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {

    val showMenuItems = rememberSaveable {
        mutableStateOf(false)
    }

    val rotationValue by animateFloatAsState(
        label = "rotationValueAnimationLabel",
        targetValue = if (showMenuItems.value) 180f else 0f
    )

    Row(
        modifier = modifier
            .wrapContentSize()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(32.dp)
            )
    ) {
        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            onClick = {
                showMenuItems.value = !showMenuItems.value
            }
        ) {
            Icon(
                modifier = Modifier.rotate(rotationValue),
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null
            )
        }


        AnimatedVisibility(showMenuItems.value) {
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .wrapContentSize()
            ) {
                IconButton(onClick = onSettingsClicked) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }

                IconButton(onClick = onConfigsClicked) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings_remote_24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}