package com.samim.jarvis.ui.voice

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VoiceAssistantScreen(viewModel: com.samim.jarvis.voice.VoiceAssistantViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                viewModel.startListening()
            } else {
                // show rationale or message
            }
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Voice Assistant", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.padding(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                // request audio permission then start
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }, modifier = Modifier.padding(end = 8.dp)) {
                Text(if (state.listening) "Listening..." else "Start Listening")
            }
            Button(onClick = { viewModel.stopListening() }) {
                Text("Stop")
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))
        Text("Last transcript:")
        Text(state.lastTranscript)

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Voice Settings", style = MaterialTheme.typography.subtitle1)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
            Text("Female voice")
            Switch(checked = state.femalePreferred, onCheckedChange = { viewModel.setFemalePreferred(it) })
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
            Text("Wake-word (Hey Jarvis)")
            Switch(checked = state.wakeWordEnabled, onCheckedChange = {
                viewModel.setWakeWordEnabled(it)
                // Start or stop foreground service for wake-word detection
                val svcIntent = Intent(context, com.samim.jarvis.voice.VoiceAssistantService::class.java)
                if (it) {
                    svcIntent.action = com.samim.jarvis.voice.VoiceAssistantService.ACTION_START
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        context.startForegroundService(svcIntent)
                    } else {
                        context.startService(svcIntent)
                    }
                } else {
                    svcIntent.action = com.samim.jarvis.voice.VoiceAssistantService.ACTION_STOP
                    context.startService(svcIntent)
                }
            })
        }

        Spacer(modifier = Modifier.padding(8.dp))
        Text("Language")
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { viewModel.setLanguage("en-US") }, modifier = Modifier.padding(end = 8.dp)) { Text("English") }
            Button(onClick = { viewModel.setLanguage("hi-IN") }) { Text("Hindi") }
        }

        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = { viewModel.speak("Hello, I am Jarvis. How can I help?") }) {
            Text("Test TTS")
        }
    }
}
