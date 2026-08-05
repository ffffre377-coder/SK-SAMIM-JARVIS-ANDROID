package com.samim.jarvis.vision

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VisionSettingsScreen() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Vision AI Settings")
        Text("Screenshot analysis and image understanding are enabled (scaffold).")
        Button(onClick = { /* placeholder: request permissions */ }) { Text("Request Screenshot Permission") }
    }
}
