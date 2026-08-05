package com.samim.jarvis.ui.voice

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VoiceAssistantScreen(viewModel: VoiceAssistantViewModel = hiltViewModel()) {
    Column {
        Text("Voice Assistant")
        Button(onClick = { viewModel.startListening() }) {
            Text("Start")
        }
        Button(onClick = { viewModel.stopListening() }) {
            Text("Stop")
        }
    }
}
