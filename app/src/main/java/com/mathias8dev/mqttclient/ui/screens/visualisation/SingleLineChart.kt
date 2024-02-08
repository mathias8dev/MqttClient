package com.mathias8dev.mqttclient.ui.screens.visualisation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.axisLineComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.lineSpec
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.shape.shader.TopBottomShader
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel


private val previewModelData =
    CartesianChartModel(LineCartesianLayerModel.build { series(-2, -1, 4, -2, 1, 5, -3) })

@Preview(backgroundColor = 0xFFFFFF, showBackground = true)
@Composable
fun SingleLineChart(
    height: Dp = 256.dp,
    isZoomEnabled: Boolean = false,
    animateChartDisplay: Boolean = false,
    model: CartesianChartModel = previewModelData
) {
    val marker = rememberMarker()
    ProvideChartStyle(rememberChartStyle(chartColors)) {
        CartesianChartHost(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    lines =
                    listOf(
                        lineSpec(
                            shader =
                            TopBottomShader(
                                DynamicShaders.color(Color(0xFF25BE53)),
                                DynamicShaders.color(Color(0xFFE73B3B)),
                            ),
                        ),
                    ),
                ),
                startAxis =
                rememberStartAxis(
                    itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = { 10 }) },
                    guideline = axisLineComponent(),
                ),
                bottomAxis =
                rememberBottomAxis(
                    guideline = axisLineComponent(),
                    label = null
                ),
            ),
            model = model,
            isZoomEnabled = isZoomEnabled,
            marker = marker,
            horizontalLayout = HorizontalLayout.fullWidth(),
        )
    }
}

private val chartColors
    @ReadOnlyComposable
    @Composable
    get() =
        listOf(
            Color(0xFF25BE53),
            Color(0xFFE73B3B),
        )