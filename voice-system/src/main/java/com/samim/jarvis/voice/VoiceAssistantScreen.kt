package com.samim.jarvis.voice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samim.jarvis.voice.ui.AppSelectionScreen
import com.samim.jarvis.voice.ui.ContactSelectionScreen
import com.samim.jarvis.voice.ui.FileSelectionScreen
import com.samim.jarvis.voice.ui.JarvisAvatar
import com.samim.jarvis.voice.ui.WaveformBar

@Composable
fun VoiceAssistantScreen(vm: VoiceAssistantViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        JarvisAvatar(state = state.avatarState)

        if (state.lastResponse.isNotBlank()) {
            Text(state.lastResponse)
        }

        if (state.listening) WaveformBar(listening = true)
        // Observe pending selection UI
        val pending by vm.pendingSelection.collectAsState()
        pending?.let { p ->
            when (p) {
                is PendingSelection.Contact -> {
                    ContactSelectionScreen(results = p.matches.map { it.first to it.second }, onSelect = { phone -> vm.acceptSelectionContact(phone) }, onCancel = { vm.clearPendingSelection() })
                }
                is PendingSelection.File -> {
                    FileSelectionScreen(results = p.matches.map { it.first to it.second }, onSelect = { uri -> vm.acceptSelectionFile(uri) }, onCancel = { vm.clearPendingSelection() })
                }
                is PendingSelection.App -> {
                    AppSelectionScreen(results = p.matches, onSelect = { pkg -> vm.acceptSelectionApp(pkg) }, onCancel = { vm.clearPendingSelection() })
                }
            }
        }

        Button(onClick = { if (state.listening) vm.stopListening() else vm.startListening() }) {
            Text(if (state.listening) "Stop" else "Tap to speak")
        }
    }
}
