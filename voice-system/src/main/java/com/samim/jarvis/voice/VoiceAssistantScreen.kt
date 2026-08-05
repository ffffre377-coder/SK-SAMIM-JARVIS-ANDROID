package com.samim.jarvis.voice

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collect
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun VoiceAssistantScreen(vm: VoiceAssistantViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showConfirm by remember { mutableStateOf(false) }
    var confirmTitle by remember { mutableStateOf("") }
    var confirmMessage by remember { mutableStateOf("") }

    // Collect events and trigger UI reactions
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                is VoiceEvent.ConfirmAction -> {
                    confirmTitle = ev.title
                    confirmMessage = ev.message
                    showConfirm = true
                }
                is VoiceEvent.Info -> {
                    // For simplicity, use Android TTS to speak short info, or you can show a Toast
                    vm.speak(ev.message)
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = {
                showConfirm = false
                vm.cancelPendingAction()
            },
            title = { Text(confirmTitle) },
            text = { Text(confirmMessage) },
            confirmButton = {
                Button(onClick = {
                    showConfirm = false
                    vm.confirmPendingAction()
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showConfirm = false
                    vm.cancelPendingAction()
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Existing UI placeholders (keep minimal to avoid breaking)
    Column {
        Text("Voice Assistant")
        Row(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
            Button(onClick = { vm.startListening() }) { Text("Start Listening") }
            Button(onClick = { vm.stopListening() }) { Text("Stop") }
        }
        Text("Last transcript: ${state.lastTranscript}")
        Text(if (state.listening) "Listening..." else "Idle")
    }
}
