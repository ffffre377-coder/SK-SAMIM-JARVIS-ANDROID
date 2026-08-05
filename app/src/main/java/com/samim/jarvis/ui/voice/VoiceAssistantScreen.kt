package com.samim.jarvis.ui.voice

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun VoiceAssistantScreen() {
    Column {
        Text("Voice Assistant")
        Button(onClick = { /* start listening */ }) {
            Text("Start")
        }
    }
}
