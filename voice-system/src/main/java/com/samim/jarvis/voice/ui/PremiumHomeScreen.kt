package com.samim.jarvis.voice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PremiumHomeScreen(onOpenAssistant: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF001722)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            // Top HUD
            Box(modifier = Modifier.fillMaxWidth()) {
                Text("SAMIM JARVIS", color = Color.White)
            }

            // central assistant card
            Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hello — I'm SAMIM JARVIS", color = Color(0xFFB3E6FF))
                    Text("Tap to speak or say 'Hey Samim'", color = Color(0xFF8FBFEF))
                    Button(onClick = onOpenAssistant, modifier = Modifier.padding(top = 16.dp), shape = RoundedCornerShape(24.dp)) {
                        Text("Start Assistant")
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 36.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                FeatureCard("Calls")
                FeatureCard("Messages")
                FeatureCard("Media")
            }
        }
    }
}

@Composable
fun FeatureCard(title: String) {
    Surface(modifier = Modifier.padding(6.dp).background(Color(0xFF061E2B), RoundedCornerShape(12.dp))) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = Color.White)
        }
    }
}
