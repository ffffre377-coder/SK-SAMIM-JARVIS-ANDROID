package com.samim.jarvis.voice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PersonalitySettingsScreen(vm: VoiceAssistantViewModel = viewModel()) {
    val current = remember { vm }
    var selected by remember { mutableStateOf(current.state.value.personalityMode ?: "Friendly") }

    Column {
        Text("Personality Mode")
        val modes = listOf("Friendly", "Funny", "Emotional", "Professional", "JARVIS", "CasualFriend", "Motivational")
        modes.forEach { mode ->
            Row(modifier = Modifier.fillMaxWidth()) {
                RadioButton(selected = (selected == mode), onClick = { selected = mode })
                Text(mode)
            }
        }
        Button(onClick = {
            val modeEnum = try {
                com.samim.jarvis.voice.personality.PersonalityMode.valueOf(selected)
            } catch (e: Exception) { com.samim.jarvis.voice.personality.PersonalityMode.Friendly }
            current.setPersonality(modeEnum)
        }) { Text("Save Personality") }
    }
}
