package com.mathias8dev.mqttclient.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed


@Stable
fun Modifier.useModifierIf(
    condition: Boolean,
    callback: (currentModifier: Modifier) -> Modifier
): Modifier {
    return if (condition) callback(this) else this
}

@Stable
fun Modifier.useComposableModifierIf(
    condition: Boolean,
    callback: @Composable (currentModifier: Modifier) -> Modifier
): Modifier = composed {
    if (condition) callback(this) else this
}