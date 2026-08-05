package com.samim.jarvis.voice.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.samim.jarvis.voice.ShellUtils

@Composable
fun BrandingSplash(onComplete: () -> Unit) {
    val transition = rememberInfiniteTransition()
    val pulse = transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1800), repeatMode = RepeatMode.Reverse)
    )

    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF001722)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Spacer(modifier = Modifier.height(80.dp))
            Box(contentAlignment = Alignment.Center) {
                // Glowing core
                Canvas(modifier = Modifier.size((120 * pulse.value).dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(listOf(Color(0xFF5BC0FF), Color(0x400A5FFF)), radius = size.minDimension / 1.2f, center = Offset(size.width/2, size.height/2)),
                        alpha = 0.9f
                    )
                }
                // Inner badge
                Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFF0A5FFF)), contentAlignment = Alignment.Center) {
                    Text("SAMIM\nJARVIS", color = Color.White, modifier = Modifier.padding(6.dp))
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            Text("Your AI Assistant", color = Color(0xFFB3E6FF))
            Spacer(modifier = Modifier.height(24.dp))
            // small animated HUD lines
            Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                val w = size.width
                val h = size.height
                val y = h / 2
                drawRect(brush = Brush.horizontalGradient(listOf(Color(0xFF5BC0FF), Color(0xFF0A5FFF)), tileMode = TileMode.Clamp), topLeft = Offset(w * 0.15f, y - 2f), size = androidx.compose.ui.geometry.Size(w * 0.7f, 4f))
            }
        }
    }
}
