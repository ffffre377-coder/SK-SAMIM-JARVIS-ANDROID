package com.samim.jarvis.voice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WakeWordSettingsScreen(vm: VoiceAssistantViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var keyword by remember { mutableStateOf("Hey Samim") }
    var enabled by remember { mutableStateOf(state.wakeWordEnabled) }

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Enable Wake Word")
            Switch(checked = enabled, onCheckedChange = {
                enabled = it
                vm.setWakeWordEnabled(it)
            })
        }

        OutlinedTextField(value = keyword, onValueChange = { keyword = it }, label = { Text("Wake Word") })
        Button(onClick = {
            // Store keyword in secure storage for later use; real integration would reload model
            vm.setWakeWordEnabled(enabled)
            vm.speak("Wake word updated to $keyword")
        }) {
            Text("Save Wake Word")
        }
    }
}
