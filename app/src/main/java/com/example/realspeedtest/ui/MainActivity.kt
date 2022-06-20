package com.example.realspeedtest.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.example.realspeedtest.R
import com.example.realspeedtest.ui.composables.components.ToggleButtonExp
import com.example.realspeedtest.ui.theme.*
import com.example.realspeedtest.ui.viewmodel.SpeedTestViewModel
import com.example.realspeedtest.utils.tests.HttpDownloadTest
import com.example.realspeedtest.utils.tests.HttpUploadTest


class MainActivity : ComponentActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        val uploadAddr = "http://speedtest1.flynet.net.br:8080/speedtest/upload.php"

        super.onCreate(savedInstanceState)
        setContent {
            RealSpeedTestTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyApp()
                }
            }
        }


    }

    @Composable
    fun MyApp() {
        SpeedTestMainPage()
    }


    @Composable
    fun SpeedTestMainPage() {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(AlivePurple, AliveMidlePurple, AliveDeepPurple)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            ToggleButtonExp()
            Spacer(modifier = Modifier.size(32.dp))
            CircularProgressbar()

        }

    }

    @Composable
    fun CircularProgressbar(
        size: Dp = 260.dp,
        foregroundIndicatorColor: Color = NeonOrange,
        shadowColor: Color = Color.LightGray,
        indicatorThickness: Dp = 24.dp,
        dataUsage: Float = 60f,
        animationDuration: Int = 1000,
        dataTextStyle: TextStyle = TextStyle(
            fontFamily = FontFamily(Font(R.font.roboto_bold, FontWeight.Bold)),
            fontSize = MaterialTheme.typography.h3.fontSize
        ),
        remainingTextStyle: TextStyle = TextStyle(
            fontFamily = FontFamily(Font(R.font.roboto_regular, FontWeight.Normal)),
            fontSize = 16.sp
        )
    ) {

        val viewModel = SpeedTestViewModel(this)

        // It remembers the data usage value
        var dataUsageRemember by remember {
            mutableStateOf(-1f)
        }

        // This is to animate the foreground indicator
        val dataUsageAnimate = animateFloatAsState(
            targetValue = dataUsageRemember,
            animationSpec = tween(
                durationMillis = animationDuration
            )
        )

        // This is to start the animation when the activity is opened
        LaunchedEffect(Unit) {
            dataUsageRemember = dataUsage
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(size),
                contentAlignment = Alignment.Center
            ) {

                Canvas(
                    modifier = Modifier
                        .size(size)
                ) {

                    // For shadow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(shadowColor, Color.White),
                            center = Offset(x = this.size.width / 2, y = this.size.height / 2),
                            radius = this.size.height / 1
                        ),
                        radius = this.size.height / 2,
                        center = Offset(x = this.size.width / 2, y = this.size.height / 2)
                    )

                    // This is the white circle that appears on the top of the shadow circle
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(AliveDeepPurple, AliveMidlePurple, AlivePurple, NeonOrange)
                        ),
                        radius = (size / 2 - indicatorThickness).toPx(),
                        center = Offset(x = this.size.width / 2, y = this.size.height / 2)
                    )

                    // Convert the dataUsage to angle
                    val sweepAngle = (dataUsageAnimate.value) * 360 / 350

                    // Foreground indicator
                    drawArc(
                        color = foregroundIndicatorColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = indicatorThickness.toPx(), cap = StrokeCap.Round),
                        size = Size(
                            width = (size - indicatorThickness).toPx(),
                            height = (size - indicatorThickness).toPx()
                        ),
                        topLeft = Offset(
                            x = (indicatorThickness / 2).toPx(),
                            y = (indicatorThickness / 2).toPx()
                        )
                    )
                }

                // Display the data usage value
                DisplayText(
                    animateNumber = dataUsageAnimate,
                    dataTextStyle = dataTextStyle,
                    remainingTextStyle = remainingTextStyle
                )
            }

            Spacer(modifier = Modifier.size(32.dp))

            ButtonProgressbar() {
                viewModel.testDownloadSpeed()
            }

            Text(
                text = "recebendo o state: ${viewModel.state}",
                color = Color.White
            )

        }
    }

    @Composable
    private fun DisplayText(
        animateNumber: State<Float>,
        dataTextStyle: TextStyle,
        remainingTextStyle: TextStyle
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Text that shows the number inside the circle
            Text(
                text = (animateNumber.value).toInt().toString() + " GB",
                style = dataTextStyle,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Remaining",
                style = remainingTextStyle,
                color = Color.White
            )
        }
    }

    @Composable
    private fun ButtonProgressbar(
        onClickButton: () -> Unit
    ) {
        Button(
            onClick = {
                onClickButton()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .size(100.dp)
                .border(1.dp, NeonOrange, shape = RoundedCornerShape(50.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(AliveDeepPurple, AliveMidlePurple, AlivePurple)
                    )
                )
        ) {
            Text(
                text = "INICIAR",
                color = Color.White
            )
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MyApp()
    }
}