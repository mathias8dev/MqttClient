package com.mathias8dev.mqttclient.ui.composables


import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mathias8dev.mqttclient.R


@Composable
fun ErrorDialog(
    @StringRes errorRes: Int,
    onDismissRequest: () -> Unit = {}
) {
    ErrorDialog(
        errorMessage = stringResource(id = errorRes),
        onDismissRequest = onDismissRequest
    )
}


@Composable
fun ErrorDialog(
    errorMessage: String = "An error occurred when trying to process your request",
    onDismissRequest: () -> Unit = {},
    onOkayClicked: ()->Unit = onDismissRequest
) {
    SimpleLottieDialog(
        message = errorMessage,
        lottieRes = R.raw.lottie_error,
        onDismissRequest = onDismissRequest,
        onOkayClicked = onOkayClicked
    )
}