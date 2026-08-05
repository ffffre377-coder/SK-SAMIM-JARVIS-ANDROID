package com.samim.jarvis.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.samim.jarvis.voice.TtsProviderManager
import dagger.hilt.android.EntryPointAccessors
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // TTS section
    val ttsManager = EntryPointAccessors.fromApplication(context, com.samim.jarvis.di.HiltEntryPoints::class.java).ttsProviderManager()
    val providers = ttsManager.listProviders()
    var selected by remember { mutableStateOf(ttsManager.getSelectedProviderName()) }
    var voiceId by remember { mutableStateOf(viewModel.secureStorage.getString("tts_voice") ?: "") }
    var speed by remember { mutableStateOf(viewModel.secureStorage.getString("tts_speed")?.toFloatOrNull() ?: 1.0f) }
    var pitch by remember { mutableStateOf(viewModel.secureStorage.getString("tts_pitch")?.toFloatOrNull() ?: 1.0f) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "AI Providers", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.padding(8.dp))

        // Existing provider cards
        state.providers.forEach { provider ->
            Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(provider.displayName, style = MaterialTheme.typography.subtitle1, modifier = Modifier.weight(1f))
                        Switch(checked = provider.enabled, onCheckedChange = { viewModel.setProviderEnabled(provider.id, it) })
                    }

                    OutlinedTextField(
                        value = provider.apiKey,
                        onValueChange = { viewModel.updateApiKey(provider.id, it) },
                        label = { Text("API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (provider.visible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.toggleVisibility(provider.id) }) {
                                Icon(imageVector = if (provider.visible) androidx.compose.material.icons.Icons.Default.VisibilityOff else androidx.compose.material.icons.Icons.Default.Visibility, contentDescription = null)
                            }
                        }
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { viewModel.saveApiKey(provider.id) }, modifier = Modifier.padding(end = 8.dp)) {
                            Text("Save")
                        }
                        Button(onClick = { viewModel.testProvider(provider.id) }) {
                            Text("Test API")
                        }
                        when (val status = provider.status) {
                            is ProviderStatus.Testing -> Text(" Testing...", modifier = Modifier.padding(start = 8.dp))
                            is ProviderStatus.Success -> Text(" Success: ${status.message}", color = MaterialTheme.colors.primary, modifier = Modifier.padding(start = 8.dp))
                            is ProviderStatus.Failure -> Text(" Failure: ${status.message}", color = MaterialTheme.colors.error, modifier = Modifier.padding(start = 8.dp))
                            else -> {}
                        }
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 12.dp))
        Text(text = "TTS Settings", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.padding(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Provider", modifier = Modifier.weight(1f))
            DropdownMenuDemo(providers, selected) { sel ->
                selected = sel
                viewModel.saveTtsProvider(sel)
            }
        }

        OutlinedTextField(value = voiceId, onValueChange = { voiceId = it }, label = { Text("Voice ID / Name") }, modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Speed: ${String.format("%.2f", speed)}")
            Slider(value = speed, onValueChange = { speed = it }, valueRange = 0.5f..2.0f)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Pitch: ${String.format("%.2f", pitch)}")
            Slider(value = pitch, onValueChange = { pitch = it }, valueRange = 0.5f..2.0f)
        }
        Row {
            Button(onClick = { viewModel.saveTtsVoice(voiceId); viewModel.saveTtsSpeed(speed); viewModel.saveTtsPitch(pitch) }) { Text("Save TTS Settings") }
            Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = { viewModel.testTts(selected, voiceId, speed, pitch) }) { Text("Test TTS") }
        }
    }
}

@Composable
fun DropdownMenuDemo(items: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selected) }

    Column {
        Button(onClick = { expanded = !expanded }) {
            Text(selectedText.ifEmpty { "Select provider" })
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(onClick = { selectedText = item; expanded = false; onSelect(item) }) {
                    Text(item)
                }
            }
        }
    }
}
