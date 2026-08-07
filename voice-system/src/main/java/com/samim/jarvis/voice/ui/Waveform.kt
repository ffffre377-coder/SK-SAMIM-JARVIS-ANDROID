package com.samim.jarvis.voice.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samim.jarvis.voice.AvatarState

@Composable
fun WaveformBar(listening: Boolean) {
    val levels = remember { List(16) { mutableStateOf(0f) } }
    LaunchedEffect(listening) {
        while (listening) {
            levels.forEach { it.value = kotlin.random.Random.nextFloat() * 0.9f + 0.1f }
            kotlinx.coroutines.delay(150)
        }
        levels.forEach { it.value = 0f }
    }

    Row(modifier = Modifier.height(40.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until levels.size) {
            val h = levels[i].value
            Box(modifier = Modifier.width(6.dp).height(40.dp * h).padding(2.dp).clip(CircleShape).background(Color.Cyan))
        }
    }
}
