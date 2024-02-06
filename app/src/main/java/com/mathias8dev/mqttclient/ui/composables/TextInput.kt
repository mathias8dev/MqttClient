package com.mathias8dev.mqttclient.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    value: String,
    placeholderText: String,
    hasErrors: Boolean,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    onErrorInfoClicked: () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (updatedValue: String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholderText,
                color = Color.Black
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (hasErrors) {
                IconButton(onClick = onErrorInfoClicked) {
                    Icon(
                        Icons.Filled.Info,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null
                    )
                }
            } else trailingIcon?.invoke()
        },
        colors = colors,
        singleLine = true,
        modifier = modifier
    )
}