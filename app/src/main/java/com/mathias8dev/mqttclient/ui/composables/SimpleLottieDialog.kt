package com.mathias8dev.mqttclient.ui.composables

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.airbnb.lottie.compose.LottieClipSpec


@Composable
fun SimpleLottieDialog(
    @StringRes messageRes: Int,
    @RawRes lottieRes: Int,
    repeatAnimationForever: Boolean = false,
    animationClipSpec: LottieClipSpec? = null,
    onDismissRequest : ()->Unit = {},
    onOkayClicked : ()->Unit = onDismissRequest,
    actionContent: @Composable (ColumnScope.()->Unit)? = null
) {
    SimpleLottieDialog(
        message = stringResource(id = messageRes),
        lottieRes = lottieRes,
        repeatAnimationForever = repeatAnimationForever,
        animationClipSpec = animationClipSpec,
        onDismissRequest = onDismissRequest,
        onOkayClicked = onOkayClicked,
        actionContent = actionContent
    )
}


@Composable
fun SimpleLottieDialog(
    message: String,
    @RawRes lottieRes: Int,
    repeatAnimationForever: Boolean = false,
    animationClipSpec: LottieClipSpec? = null,
    onDismissRequest : ()->Unit = {},
    onOkayClicked: () -> Unit = onDismissRequest,
    actionContent: @Composable (ColumnScope.()->Unit)? = null
) {
    StandardDialog(
        onDismissRequest = onDismissRequest
    ) {
        LottieAnimation(
            animationRes = lottieRes,
            repeatForever = repeatAnimationForever,
            clipSpec = animationClipSpec,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(72.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = message,
            textAlign = TextAlign.Center
        )

        actionContent?.invoke(this) ?: Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp),
            onClick = onOkayClicked
        ) {
            Text("OK")
        }
    }
}