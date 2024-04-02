package com.breathingcapybara.networkdisconnectedlabel

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.breathingcapybara.networkdisconnectedlabel.ui.theme.NetworkDisconnectedLabelTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkDisconnectedLabelTheme {
                NetworkDisconnectedBody(vm) { }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startService()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    private var connectivityManager: ConnectivityManager? = null
    private fun startService() {   // 서비스 실행 버튼
        if (connectivityManager == null) {
            connectivityManager = getSystemService(ConnectivityManager::class.java).apply {
                registerDefaultNetworkCallback(networkStrengthCallback)
            }
        }
    }

    private fun stopService() { // 서비스 중지 버튼
        if (connectivityManager != null) {
            connectivityManager?.unregisterNetworkCallback(networkStrengthCallback)
        }
    }

    private val networkStrengthCallback = object : ConnectivityManager.NetworkCallback() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities,
        ) {
            val strength = networkCapabilities.signalStrength
            saveNetworkStrength(strength)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            saveNetworkStrength(0)
        }
    }

    private fun saveNetworkStrength(strength: Int) {
        vm.setNetworkStrength(strength)
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkDisconnectedPreview() {
    NetworkDisconnectedLabelTheme {
        NetworkDisconnectedBox {

        }
    }
}

@Composable
fun NetworkDisconnectedBody(
    vm: MainViewModel,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val network by remember { vm.networkStrengthState }

        NetworkDisconnectedBox(
            network = { network },
            content = content
        )
    }
}

@Composable
private fun NetworkDisconnectedBox(
    network: () -> Int = { 0 },
    content: @Composable (BoxScope.() -> Unit)
) {
    val alpha by animateFloatAsState(
        targetValue = if (network() < -25) 0f else .5f * (network() + 25) / 25,
        label = "network display alpha"
    )

    val textMeasurer = rememberTextMeasurer()
    val textToDraw = "  Network Disconnected".repeat(6)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                rotate(-10f) {
                    drawRect(
                        color = Color.Yellow.copy(alpha * 2),
                        topLeft = Offset(-50f, size.height / 2 - 10),
                        size = Size(this.size.width + 100, 80f)
                    )
                    drawRect(
                        color = Color.Black.copy(alpha * 2),
                        topLeft = Offset(-50f, size.height / 2 - 10),
                        size = Size(this.size.width + 100, 80f),
                        style = Stroke(width = 2f),
                    )
                    if (alpha != 0f)
                        drawText(
                            textMeasurer = textMeasurer,
                            text = textToDraw,
                            topLeft = Offset(0f, size.height / 2),
                            maxLines = 1,
                            style = TextStyle.Default.copy(
                                fontSize = 20.sp,
                                color = Color.Black
                            ),
                            size = Size(this.size.width + 100, 80f),
                        )
                }
                rotate(-15f) {
                    drawRect(
                        color = Color.Yellow.copy(alpha * 2),
                        topLeft = Offset(-75f, size.height / 2 - 10),
                        size = Size(this.size.width + 150, 80f)
                    )
                    drawRect(
                        color = Color.Black.copy(alpha * 2),
                        topLeft = Offset(-75f, size.height / 2 - 10),
                        size = Size(this.size.width + 150, 80f),
                        style = Stroke(width = 2f),
                    )
                    if (alpha != 0f)
                        drawText(
                            textMeasurer = textMeasurer,
                            text = textToDraw,
                            topLeft = Offset(0f, size.height / 2),
                            maxLines = 1,
                            style = TextStyle.Default.copy(
                                fontSize = 20.sp,
                                color = Color.Black
                            ),
                            size = Size(this.size.width + 150, 80f),
                        )
                }
            },
        contentAlignment = Alignment.BottomStart,
    ) {
        content()
        Text(
            "network:${network()}",
            color = Color.Red,
            fontSize = 10.sp.times(1 + alpha * 2)
        )
    }
}