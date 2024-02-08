package com.mathias8dev.mqttclient.ui.screens.visualisation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mathias8dev.mqttclient.R
import timber.log.Timber


@Composable
fun FloatingMenu(
    modifier: Modifier = Modifier,
    makeMenuMovable: Boolean,
    onConfigsClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {

    val showMenuItems = rememberSaveable {
        mutableStateOf(false)
    }

    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { _, offsetChange, _ ->
        offset += offsetChange
    }

    val rotationValue by animateFloatAsState(
        label = "rotationValueAnimationLabel",
        targetValue = if (showMenuItems.value) 180f else 0f
    )

    val movable  by remember{ mutableStateOf(makeMenuMovable) }

    LaunchedEffect(key1 = movable, block = {
        Timber.d("Make menu movable $movable")
    })

    val movableModifier = modifier
        .graphicsLayer {
            if (movable) {
                translationX = offset.x
                translationY = offset.y
            }
        }
        .transformable(state)

    Row(
        modifier = movableModifier
            .wrapContentSize()
            .padding(8.dp)
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(32.dp),
                color = Color(0XFF505050)
            )
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