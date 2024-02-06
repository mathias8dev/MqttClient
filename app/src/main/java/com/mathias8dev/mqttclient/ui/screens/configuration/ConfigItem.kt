package com.mathias8dev.mqttclient.ui.screens.configuration

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathias8dev.mqttclient.R


@Composable
fun ConfigItem(
    @DrawableRes drawableRes: Int,
    title: String,
    value: String,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Row(
        modifier = Modifier.wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(id = drawableRes),
            contentDescription = null,
            tint = textColor
        )
        Column(modifier = Modifier.wrapContentSize()) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = textColor.copy(0.9f)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                color = textColor
            )
        }
    }
}