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
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // TTS provider list from ViewModel
    val providers = viewModel.getAvailableTtsProviders()
    var selected by remember { mutableStateOf(viewModel.getSelectedTtsProvider()) }
    var voiceId by remember { mutableStateOf(viewModel.getSavedTtsVoice()) }
    var speed by remember { mutableStateOf(viewModel.getSavedTtsSpeed()) }
    var pitch by remember { mutableStateOf(viewModel.getSavedTtsPitch()) }
    var elevenKey by remember { mutableStateOf(viewModel.getTtsApiKey("elevenlabs") ?: "") }
    val ttsTestStatus by viewModel.ttsTestStatus.collectAsState()

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

        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(value = elevenKey, onValueChange = { elevenKey = it }, label = { Text("ElevenLabs API Key") }, modifier = Modifier.fillMaxWidth(), visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { viewModel.saveTtsApiKey("elevenlabs", elevenKey); Toast.makeText(context, "Saved ElevenLabs key", Toast.LENGTH_SHORT).show() }) { Text("Save API Key") }
            Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = { viewModel.clearTtsApiKey("elevenlabs"); elevenKey = "" }) { Text("Clear") }
        }

        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(value = voiceId, onValueChange = { voiceId = it }, label = { Text("Voice ID / Name") }, modifier = Modifier.fillMaxWidth())

        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Female natural voice")
            Switch(checked = viewModel.getSavedFemalePref(), onCheckedChange = { viewModel.setFemalePref(it) })
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Speed: ${String.format("%.2f", speed)}", modifier = Modifier.weight(1f))
            Slider(value = speed, onValueChange = { speed = it }, valueRange = 0.5f..2.0f, modifier = Modifier.weight(2f))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Pitch: ${String.format("%.2f", pitch)}", modifier = Modifier.weight(1f))
            Slider(value = pitch, onValueChange = { pitch = it }, valueRange = 0.5f..2.0f, modifier = Modifier.weight(2f))
        }

        Spacer(modifier = Modifier.padding(8.dp))
        Row {
            Button(onClick = { viewModel.saveTtsVoice(voiceId); viewModel.saveTtsSpeed(speed); viewModel.saveTtsPitch(pitch); Toast.makeText(context, "Saved TTS settings", Toast.LENGTH_SHORT).show() }) { Text("Save TTS Settings") }
            Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = { viewModel.testTts(selected, voiceId, speed, pitch) }) { Text("Test Voice") }
        }

        when (val t = ttsTestStatus) {
            is TtsTestState.Testing -> Text("Testing voice...", modifier = Modifier.padding(top = 8.dp))
            is TtsTestState.Success -> Text("Success: ${t.message}", color = MaterialTheme.colors.primary, modifier = Modifier.padding(top = 8.dp))
            is TtsTestState.Failure -> Text("Failure: ${t.message}", color = MaterialTheme.colors.error, modifier = Modifier.padding(top = 8.dp))
            else -> {}
        }
    }
}
