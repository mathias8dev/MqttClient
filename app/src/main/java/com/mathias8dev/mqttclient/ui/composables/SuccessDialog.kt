package com.mathias8dev.mqttclient.ui.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mathias8dev.mqttclient.R

@Composable
fun SuccessDialog(
    @StringRes messageRes: Int,
    onDismissRequest : ()->Unit = {}
) {
    SuccessDialog(
        message = stringResource(id = messageRes),
        onDismissRequest = onDismissRequest
    )
}


@Composable
fun SuccessDialog(
    message: String,
    onDismissRequest : ()->Unit = {}
) {
    SimpleLottieDialog(
        message = message,
        lottieRes = R.raw.lottie_success,
        onDismissRequest = onDismissRequest
    )
}