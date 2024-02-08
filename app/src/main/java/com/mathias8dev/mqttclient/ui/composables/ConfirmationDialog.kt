package com.mathias8dev.mqttclient.ui.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieClipSpec
import com.mathias8dev.mqttclient.R


@Composable
fun ConfirmationDialog(
    @StringRes messageRes: Int,
    onDismissRequest: () -> Unit,
    onConfirmClicked: () -> Unit,
    onCancelClicked: () -> Unit = onDismissRequest
) {
    ConfirmationDialog(
        message = stringResource(id = messageRes),
        onDismissRequest = onDismissRequest,
        onConfirmClicked = onConfirmClicked,
        onCancelClicked = onCancelClicked
    )
}


@Composable
fun ConfirmationDialog(
    message: String,
    onDismissRequest: () -> Unit = {},
    onConfirmClicked: () -> Unit,
    onCancelClicked: () -> Unit = onDismissRequest
) {
    SimpleLottieDialog(
        message = message,
        lottieRes = R.raw.lottie_question,
        animationClipSpec = LottieClipSpec.Progress(0f, 0.8f),
        onDismissRequest = onDismissRequest,
        actionContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                TextButton(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = onCancelClicked
                ) {
                    Text("NON")
                }

                Button(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = onConfirmClicked
                ) {
                    Text("OUI")
                }
            }
        }
    )
}