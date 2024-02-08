package com.mathias8dev.mqttclient.ui.screens.visualisation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mathias8dev.mqttclient.R
import com.mathias8dev.mqttclient.ui.composables.LottieAnimation
import com.patrykandpatrick.vico.core.model.CartesianChartModel


@Composable
fun MeasurementDisplay(
    modifier: Modifier = Modifier,
    chartTitle: String,
    chartHeight: Dp = 256.dp,
    chartModel: CartesianChartModel?,
    isZoomEnabled: Boolean = true,
    animateChartDisplay: Boolean = true,
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = chartTitle,
                style = MaterialTheme.typography.titleSmall
            )
            if (chartModel == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieAnimation(
                            modifier = Modifier.size(92.dp),
                            animationRes = R.raw.lottie_empty,
                            repeatForever = true
                        )
                        Text(
                            text = "Aucune données disponible",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                SingleLineChart(
                    height = chartHeight,
                    model = chartModel,
                    isZoomEnabled = isZoomEnabled,
                    animateChartDisplay = animateChartDisplay
                )
            }
        }
    }
}

@Composable
fun MeasurementDisplay(
    modifier: Modifier = Modifier,
    @StringRes chartTitleRes: Int,
    chartHeight: Dp = 256.dp,
    chartModel: CartesianChartModel?,
    isZoomEnabled: Boolean = true,
    animateChartDisplay: Boolean = true,
) {
    MeasurementDisplay(
        modifier = modifier,
        chartTitle = stringResource(id = chartTitleRes),
        chartHeight = chartHeight,
        chartModel = chartModel,
        isZoomEnabled = isZoomEnabled,
        animateChartDisplay = animateChartDisplay
    )
}