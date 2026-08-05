package com.samim.jarvis.voice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Settings UI for managing voice providers. Uses Compose and VoiceProviderViewModel.
 * This screen allows viewing supported providers, adding/editing API keys, selecting default provider, and testing connections.
 */
@Composable
fun VoiceProviderSettingsScreen(vm: VoiceProviderViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    var editingKey by remember { mutableStateOf("") }
    var editingName by remember { mutableStateOf("") }

    Column {
        Text("Voice Providers")
        LazyColumn {
            items(state.providers) { p ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(p.name)
                        Text(text = "Voice: ${p.voiceId ?: "default"}")
                        Text(text = if (p.enabled) "Enabled" else "Disabled")
                    }
                    Row {
                        Button(onClick = {
                            editingName = p.name
                            editingKey = p.apiKey ?: ""
                        }) {
                            Text("Edit Key")
                        }
                        Button(onClick = { vm.testConnection(p.name) }) {
                            Text("Test")
                        }
                        Switch(checked = (state.selectedProvider == p.name), onCheckedChange = {
                            if (it) vm.selectProvider(p.name)
                        })
                    }
                }
            }
        }

        if (editingName.isNotBlank()) {
            OutlinedTextField(value = editingKey, onValueChange = { editingKey = it }, label = { Text("API Key for $editingName") })
            Row {
                Button(onClick = {
                    vm.saveApiKey(editingName, editingKey)
                    editingName = ""
                    editingKey = ""
                }) { Text("Save") }
                Button(onClick = { vm.deleteApiKey(editingName); editingName = ""; editingKey = "" }) { Text("Delete") }
            }
        }

        Text(state.statusMessage)
    }
}
