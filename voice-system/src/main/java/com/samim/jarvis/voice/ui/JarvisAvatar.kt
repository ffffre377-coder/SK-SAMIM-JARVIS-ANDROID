package com.samim.jarvis.voice.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.samim.jarvis.voice.AvatarState

@Composable
fun JarvisAvatar(
    state: AvatarState,
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF00BCD4),
    emotionLevel: Int = 50
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )

    val mouthScale by remember(state) { mutableStateOf(1f) }

    Surface(modifier = modifier.size(180.dp), color = Color.Transparent) {
        Box(contentAlignment = Alignment.Center) {
            // Outer glow circle
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(color = baseColor.copy(alpha = 0.12f), radius = size.minDimension / 2 * pulse)
                drawCircle(color = baseColor.copy(alpha = 0.06f), radius = size.minDimension / 2 * pulse * 1.2f)
            }

            // Face circle
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(baseColor.copy(alpha = 0.95f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Eyes
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.White))
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.White))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mouth / speaking indicator
                    when (state) {
                        AvatarState.Listening -> {
                            Canvas(modifier = Modifier.size(60.dp)) {
                                drawCircle(color = Color.Black.copy(alpha = 0.8f), radius = size.minDimension / 4)
                            }
                        }
                        AvatarState.Thinking -> {
                            // dots
                            Row {
                                repeat(3) {
                                    Box(modifier = Modifier.size(8.dp).background(Color.White).padding(4.dp))
                                }
                            }
                        }
                        AvatarState.Speaking -> {
                            // Mouth animated
                            val mouthAnim by rememberInfiniteTransition().animateFloat(0.6f, 1.0f, animationSpec = infiniteRepeatable(tween(200), RepeatMode.Reverse))
                            Canvas(modifier = Modifier.size(60.dp)) {
                                drawCircle(color = Color.Black, radius = size.minDimension / 4 * mouthAnim)
                            }
                        }
                        else -> {
                            Canvas(modifier = Modifier.size(60.dp)) {
                                drawCircle(color = Color.Black.copy(alpha = 0.6f), radius = size.minDimension / 6)
                            }
                        }
                    }
                }
            }
        }
    }
}
